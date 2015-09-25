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
     * Get a reference to our access token. If the access token is not valid, redirect the user back to the login screen
     *
     * @return true if the user must be redirected back to the login screen else false.
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
        //Perform this before redirecting to login to avoid null pointer exceptions in the subclasses since they may try to access their views inside the onCreate method and the views wont be loaded unless the ViewStub is inflated here
        initSubclassLayout();
        //If we don't have a valid access token or its null, redirect the person back to login screen
        if (shouldRedirectToLogin()) {
            //Prevent further processing, calling finish() does not quit your activity immediately, it still runs code after finish() in the current method
            return;
        }
        //The part of code below doesn't execute if the access token is null or invalid
        initAppBar();
        initDrawer(savedInstanceState);
    }

    /**
     * Calling setupDrawer inside the onCreate, results in a situation where the onCreate, onCreateView and onViewCreated of FragmentDrawer are not yet executed and the drawer does not associate properly with the DrawerLayout. Call setupDrawer inside onResume so that onCreate, onCreateView, onViewCreated are first executed and then this method runs inside the FragmentDrawer.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mDrawer.setupDrawer(mToolbar, mDrawerLayout);
    }

    /**
     * Call this method before calling shouldRedirectToLogin to prevent crashes in the sub activities that implement this activity. The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation.
     */
    private void initSubclassLayout() {
        mMainContent = (ViewStub) findViewById(R.id.main_content);
        mMainContent.setInflatedId(getRootViewId());
        mMainContent.setLayoutResource(getLayoutForActivity());
        mMainContent.inflate();
    }

    /**
     * Initialize the Toolbar and Tab Layout and if we have a valid View Pager id from the subclasses and a valid Pager Adapter object, then link the Tab Layout with that View Pager else , hide the Tab Layout.
     */
    private void initAppBar() {
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

    /**
     * If the drawer is created in XML, it runs code inside its onCreate, onCreateView, onViewCreated even before shouldRedirectToLogin is triggered executing unnecessary code. Rather create the drawer in code, and replace it everytime the Activity is started after a rotation.
     *
     * @param savedInstanceState
     */
    private void initDrawer(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mDrawer = new FragmentDrawer_();
        } else {
            mDrawer = (FragmentDrawer_) getFragmentManager().findFragmentByTag(DRAWER_FRAGMENT_TAG);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getFragmentManager().beginTransaction().replace(R.id.drawer_frame_layout, mDrawer, DRAWER_FRAGMENT_TAG).commit();
    }

    public void beforeFeedLoaded(AccessToken accessToken, @NonNull Group group) {
        onFeedLoaded(accessToken, group);
    }

    @Background
    void onFeedLoaded(AccessToken accessToken, @NonNull Group group) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            JSONArray jsonArray = FBUtils.requestFeedSync(accessToken, group);
            DataStore.storeFeed(realm, jsonArray);
            afterFeedLoaded();
        } catch (JSONException e) {
            L.m("" + e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @UiThread
    void afterFeedLoaded() {

    }

    /**
     * If the Navigation Drawer did not handle the back press, then let the Activity execute the default behavior to handle back press
     */
    @Override
    public void onBackPressed() {
        if (!mDrawer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * @return the xml layout resource for your Activity that extends from our current one.
     */
    @NonNull
    @LayoutRes
    public abstract int getLayoutForActivity();

    /**
     * @return the ID of your root View or ViewGroup inside your Activity's layout XML file that extends from our current one
     */
    @NonNull
    @IdRes
    public abstract int getRootViewId();

    /**
     * @return the id of the View Pager inside the Activity's layout XML file that extends from our current one or 0 if the Activity doesn't have a View Pager.
     */
    @Nullable
    @IdRes
    public abstract int getViewPagerId();

    /**
     * @return the PagerAdapter object that will be used to link with the ViewPager returned from getViewPagerId(). If child Activity does not use a View Pager simply return a null here and the Tab Layout won't be shown to child Activity.
     */
    @Nullable
    public abstract PagerAdapter getPagerAdapter();

}

