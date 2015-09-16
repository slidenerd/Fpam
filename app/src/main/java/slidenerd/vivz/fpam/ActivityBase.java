package slidenerd.vivz.fpam;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;

import com.facebook.AccessToken;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.FBPost;
import slidenerd.vivz.fpam.model.json.group.FBGroup;
import slidenerd.vivz.fpam.util.DiskUtils;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * Created by vivz on 06/08/15.
 */
@EActivity
public abstract class ActivityBase extends AppCompatActivity {

    private FragmentDrawer_ mDrawer;
    private ViewStub mMainContent;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    /**
     * Get a reference to our access token. If its not valid, then let the user login once again through the Login screen.
     */
    private void redirectToLogin(AccessToken accessToken) {
        NavUtils.startActivityLogin(this);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //call this method first to prevent crashes in the sub activities that implement this activity
        initChildActivityLayout();
        //if we dont have a valid access token or its null, redirect the person back to login screen
        AccessToken accessToken = FpamApplication.getFacebookAccessToken();
        if (!FBUtils.isValidToken(accessToken)) {
            redirectToLogin(accessToken);
            //Prevent further processing, calling finish() does not quit your activity immediately, it still runs code after finish() in the current method
            return;
        }
        //The statements below wont be run if access token is null or invalid
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        if (savedInstanceState == null) {
            mDrawer = new FragmentDrawer_();
        } else {
            mDrawer = (FragmentDrawer_) getFragmentManager().findFragmentByTag("fragment_drawer");
        }
        getFragmentManager().beginTransaction().replace(R.id.drawer_frame_layout, mDrawer, "fragment_drawer").commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawer.initDrawer(mToolbar, mDrawerLayout);
    }

    private void initChildActivityLayout() {
        mMainContent = (ViewStub) findViewById(R.id.main_content);
        mMainContent.setInflatedId(getRootViewId());
        mMainContent.setLayoutResource(getLayoutForActivity());
        mMainContent.inflate();
    }

    /**
     * The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation. First initialize the Toolbar that represents our Action Bar. Initialize our Navigation Drawer and configure it. Decide whether we need to show the drawer or not. Check if the user selected an item previously from the drawer, if yes set that item to be the currently selected item inside the drawer, otherwise let the currently selected item be 'Settings'
     */


    @Background
    void loadFeedAsync(AccessToken accessToken, FBGroup group) {
        if (group == null)
            return;
        ArrayList<FBGroup> listGroups = new ArrayList<>();
        ArrayList<FBPost> listPosts = new ArrayList<>();
        listGroups.add(group);
        try {
            listPosts = FBUtils.requestFeedSync(accessToken, listGroups);
        } catch (JSONException e) {
            L.m("" + e);
        }
        onFeedLoaded(listPosts);
    }

    @UiThread
    void onFeedLoaded(ArrayList<FBPost> listPosts) {
        FBGroup group = mDrawer.getSelectedGroup();
        String data = group.getName() + "\n" + listPosts.toString();
        DiskUtils.writeToCache(ActivityBase.this, data);
    }

    @Override
    public void onBackPressed() {
        if (!mDrawer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * @return the xml layout resource for your Activity that implements this one.
     */
    @LayoutRes
    public abstract int getLayoutForActivity();

    /**
     * @return the ID of your root View inside your Activity's layout XML file
     */
    @IdRes
    public abstract int getRootViewId();
}

