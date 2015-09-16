package slidenerd.vivz.fpam;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;

import com.facebook.AccessToken;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.FBPost;
import slidenerd.vivz.fpam.model.json.group.FBGroup;
import slidenerd.vivz.fpam.prefs.MyPrefs_;
import slidenerd.vivz.fpam.util.DiskUtils;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * Created by vivz on 06/08/15.
 */
@EActivity
public abstract class ActivityBase extends AppCompatActivity {


    @Pref
    MyPrefs_ mSharedPreferences;
    private FragmentDrawer mDrawer;
    private ViewStub mMainContent;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    /*
    The Drawer Listener responsible for providing a handy way to tie together the functionality of DrawerLayout and the framework ActionBar to implement the recommended design for navigation drawers.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /*
    A variable indicating whether the user has seen the drawer before. If the user has launched this app for the very first time, we want to open the drawer and show them its existence but if the user has started the app more than once, we keep the drawer closed.
     */
    private boolean mUserSawDrawer = false;

    /**
     * Get a reference to our access token. If its not valid, then let the user login once again through the Login screen.
     */
    private void redirectToLogin(AccessToken accessToken) {
        if (!FBUtils.isValidToken(accessToken)) {
            NavUtils.startActivityLogin(this);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        redirectToLogin(accessToken);
        initUI(savedInstanceState);
    }

    /**
     * The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation. First initialize the Toolbar that represents our Action Bar. Initialize our Navigation Drawer and configure it. Decide whether we need to show the drawer or not. Check if the user selected an item previously from the drawer, if yes set that item to be the currently selected item inside the drawer, otherwise let the currently selected item be 'Settings'
     */
    void initUI(Bundle savedInstanceState) {
        mDrawer = (FragmentDrawer) getFragmentManager().findFragmentByTag("fragment_drawer");
        mMainContent = (ViewStub) findViewById(R.id.main_content);
        mMainContent.setInflatedId(getRootViewId());
        mMainContent.setLayoutResource(getLayoutForActivity());
        mMainContent.inflate();
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
    }

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private boolean didUserSeeDrawer() {
        mUserSawDrawer = mSharedPreferences.firstTime().get();
        return mUserSawDrawer;
    }

    private void markDrawerSeen() {
        mUserSawDrawer = true;
        mSharedPreferences.edit().firstTime().put(true).apply();
    }

    void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            hideDrawer();
        } else {
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

