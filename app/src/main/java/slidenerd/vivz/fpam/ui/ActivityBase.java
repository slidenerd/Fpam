package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

import com.facebook.AccessToken;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;

import io.realm.Realm;
import slidenerd.vivz.fpam.ApplicationFpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * Created by vivz on 06/08/15.
 */
@EActivity
public abstract class ActivityBase extends AppCompatActivity {

    public static final String DRAWER_FRAGMENT_TAG = "fragment_drawer";
    private FragmentDrawer_ mDrawer;
    private ViewStub mMainContent;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawerLayout;

    /**
     * Get a reference to our access token. If its not valid, then let the user login once again through the Login screen.
     */
    private boolean shouldRedirectToLogin() {
        AccessToken accessToken = ApplicationFpam.getFacebookAccessToken();
        if (!FBUtils.isValidToken(accessToken)) {
            NavUtils.startActivityLogin(this);
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //Perform this before the redirect to login check to avoid null pointer exceptions
        initSubclassLayout();
        //if we dont have a valid access token or its null, redirect the person back to login screen
        if (shouldRedirectToLogin()) {
            //Prevent further processing, calling finish() does not quit your activity immediately, it still runs code after finish() in the current method
            return;
        }
        initAppBar();
        initDrawer(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Calling initDrawer inside the onCreate, results in a situation where the onCreate, onCreateView and onViewCreated of FragmentDrawer are not yet executed and the drawer does not associate properly with the DrawerLayout. Call this method here so that onCreate, onCreateView, onViewCreated are first executed and then this method runs inside the FragmentDrawer.
        mDrawer.initDrawer(mToolbar, mDrawerLayout);
    }

    /**
     * call this method before calling shouldRedirectToLogin to prevent crashes in the sub activities that implement this activity
     */
    private void initSubclassLayout() {
        /**
         * The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation.
         */
        mMainContent = (ViewStub) findViewById(R.id.main_content);
        mMainContent.setInflatedId(getRootViewId());
        mMainContent.setLayoutResource(getLayoutForActivity());
        mMainContent.inflate();
    }

    private void initAppBar() {
        //The statements below wont be run if access token is null or invalid
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        setSupportActionBar(mToolbar);
        int viewPagerId = getViewPagerId();
        PagerAdapter pagerAdapter = getPagerAdapter();
        if (viewPagerId != 0 && pagerAdapter != null) {
            ViewPager pager = (ViewPager) findViewById(viewPagerId);
            pager.setAdapter(pagerAdapter);
            mTabLayout.setupWithViewPager(pager);
        } else {
            mTabLayout.setVisibility(View.GONE);
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        //If the drawer is created in XML, it runs code inside its onCreate, onCreateView, onViewCreated even before shouldRedirectToLogin is triggered executing unnecessary code. Rather create the drawer in code, and replace it everytime the Activity is started after a rotation.
        if (savedInstanceState == null) {
            mDrawer = new FragmentDrawer_();
        } else {
            mDrawer = (FragmentDrawer_) getFragmentManager().findFragmentByTag(DRAWER_FRAGMENT_TAG);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getFragmentManager().beginTransaction().replace(R.id.drawer_frame_layout, mDrawer, DRAWER_FRAGMENT_TAG).commit();
    }

    public void loadFeed(AccessToken accessToken, @NonNull Group group) {
        loadFeedAsync(accessToken, group);
    }

    @Background
    void loadFeedAsync(AccessToken accessToken, @NonNull Group group) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            JSONArray jsonArray = FBUtils.requestFeedSync(accessToken, group);
            DataStore.storeFeed(realm, jsonArray);
            onFeedLoaded();
        } catch (JSONException e) {
            L.m("" + e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @UiThread
    void onFeedLoaded() {

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

    @Nullable
    @IdRes
    public abstract int getViewPagerId();

    @Nullable
    public abstract PagerAdapter getPagerAdapter();

}

