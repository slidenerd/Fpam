package slidenerd.vivz.fpam;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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

import io.realm.Realm;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.RealmPost;
import slidenerd.vivz.fpam.util.DateUtils;
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
    private ProgressDialog mProgress;

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
        L.t(this, DateUtils.getUTCTimestamp() + "");
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
        //If the drawer is created in XML, it runs code inside its onCreate, onCreateView, onViewCreated even before redirectToLogin is triggered executing unnecessary code. Rather create the drawer in code, and replace it everytime the Activity is started after a rotation.
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
        //Calling initDrawer inside the onCreate, results in a situation where the onCreate, onCreateView and onViewCreated of FragmentDrawer are not yet executed and the drawer does not associate properly with the DrawerLayout. Call this method here so that onCreate, onCreateView, onViewCreated are first executed and then this method runs inside the FragmentDrawer.
        mDrawer.initDrawer(mToolbar, mDrawerLayout);
    }

    /**
     * call this method before calling redirectToLogin to prevent crashes in the sub activities that implement this activity
     */
    private void initChildActivityLayout() {
        /**
         * The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation.
         */
        mMainContent = (ViewStub) findViewById(R.id.main_content);
        mMainContent.setInflatedId(getRootViewId());
        mMainContent.setLayoutResource(getLayoutForActivity());
        mMainContent.inflate();
    }

    public void loadFeed(AccessToken accessToken, @NonNull Group group) {
        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setTitle(group.getName());
        mProgress.setMessage("Loading posts from " + group.getName());
        mProgress.show();
        loadFeedAsync(accessToken, group);
    }

    @Background
    void loadFeedAsync(AccessToken accessToken, @NonNull Group group) {
        try {
            ArrayList<Post> listPosts = FBUtils.requestFeedSync(accessToken, FpamApplication.getGson(), group);
            ArrayList<RealmPost> listRealmPosts = new ArrayList<>(listPosts.size());
            for (Post post : listPosts) {
                RealmPost realmPost = new RealmPost();
                realmPost.setId(post.getId());
                realmPost.setName(post.getName());
                realmPost.setCaption(post.getCaption());
                realmPost.setDescription(post.getDescription());
                realmPost.setLink(post.getLink());
                realmPost.setType(post.getType());
                realmPost.setUpdatedTime(post.getUpdatedTime());
                listRealmPosts.add(realmPost);
            }
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(listRealmPosts);
            realm.commitTransaction();
            realm.close();
            onFeedLoaded();
        } catch (JSONException e) {
            L.m("" + e);
        }
    }

    @UiThread
    void onFeedLoaded() {
        mProgress.hide();
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

