package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import io.realm.Realm;
import slidenerd.vivz.fpam.ApplicationFpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Feed;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.prefs.MyPrefs_;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.JSONUtils;
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * Created by vivz on 06/08/15.
 */
@EActivity
public abstract class ActivityBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String DRAWER_FRAGMENT_TAG = "fragment_drawer";
    @Pref
    MyPrefs_ mPref;
    @InstanceState
    int mSelectedMenuId;

    private Group mSelectedGroup;
    /*
    The Drawer Listener responsible for providing a handy way to tie together the functionality of DrawerLayout and the framework ActionBar to implement the recommended design for navigation drawers.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentDrawer_ mDrawer;
    private ViewStub mStub;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawerLayout;
    private AccessToken mAccessToken;


    /**
     * Get a reference to our access token. If the access token is not valid, redirect the user back to the login screen
     *
     * @return true if the user must be redirected back to the login screen else false.
     */
    private boolean shouldRedirectToLogin() {
        if (!FBUtils.isValidToken(mAccessToken)) {
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
        mAccessToken = ApplicationFpam.getFacebookAccessToken();
        //Perform this before redirecting to login to avoid null pointer exceptions in the subclasses since they may try to access their views inside the onCreate method and the views wont be loaded unless the ViewStub is inflated here
        initSubclassLayout();
        //If we don't have a valid access token or its null, redirect the person back to login screen
        if (shouldRedirectToLogin()) {
            //Prevent further processing, calling finish() does not quit your activity immediately, it still runs code after finish() in the current method
            return;
        }
        //The part of code below doesn't execute if the access token is null or invalid
        initUI(savedInstanceState);
        initTabs();
    }


    /**
     * Call this method before calling shouldRedirectToLogin to prevent crashes in the sub activities that implement this activity. The ViewStub into which the actual layout of the subclasses implementing this Activity is loaded. Each subclass Activity is expected to have a simple container or Layout and specify its id and layout resource file name when they implement this Activity. This needs to be done regardless of whether the Activity starts for the first time or starts after a subsequent rotation.
     */
    private void initSubclassLayout() {
        mStub = (ViewStub) findViewById(R.id.main_content);
        mStub.setInflatedId(getRootViewId());
        mStub.setLayoutResource(getLayoutForActivity());
        mStub.inflate();
    }

    /**
     * Initialize the Toolbar and Tab Layout and if we have a valid View Pager id from the subclasses and a valid Pager Adapter object, then link the Tab Layout with that View Pager else , hide the Tab Layout.
     */
    private void initTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
    private void initUI(Bundle savedInstanceState) {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(mToolbar);
        if (savedInstanceState == null) {
            mDrawer = new FragmentDrawer_();
        } else {
            mDrawer = (FragmentDrawer_) getFragmentManager().findFragmentByTag(DRAWER_FRAGMENT_TAG);
            mSelectedGroup = Parcels.unwrap(savedInstanceState.getParcelable("selectedGroup"));
        }
        getFragmentManager().beginTransaction().replace(R.id.drawer_frame_layout, mDrawer, DRAWER_FRAGMENT_TAG).commit();
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            show();
            markDrawerSeen();
        }
        setTitle(mSelectedGroup == null ? getString(R.string.title_activity_main) : mSelectedGroup.getName());
    }

    @Background
    void loadFeed(@NonNull Group group) {
        if (FBUtils.isValidToken(mAccessToken)) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                JSONObject feedObject = FBUtils.requestFeedSync(mAccessToken, group);
                Feed feed = JSONUtils.loadFeedFrom(group.getId(), feedObject);
                DataStore.storeFeed(realm, feed);
                onFeedLoaded("Feed Loaded For", group);
            } catch (JSONException e) {
                L.m("" + e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        } else {
            onFeedLoaded("Did not find a valid access token while loading", group);
        }
    }

    @UiThread
    void onFeedLoaded(String message, Group group) {
        L.t(ActivityBase.this, message + group.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedGroup", Parcels.wrap(Group.class, mSelectedGroup));
    }

    /**
     * If the Navigation Drawer did not handle the back press, then let the Activity execute the default behavior to handle back press
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            hide();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedMenuId = menuItem.getItemId();
        return navigate();
    }

    boolean navigate() {
        switch (mSelectedMenuId) {
            case R.id.menu_settings:
                hide();
                break;
            case R.id.menu_logout:
                logout();
                hide();
                NavUtils.startActivityLogin(this);
                finish();
                break;
            default:
                mSelectedGroup = mDrawer.getSelectedGroup(mSelectedMenuId);
                if (mSelectedGroup != null) {
                    setTitle(mSelectedGroup.getName());
                    loadFeed(mSelectedGroup);
                }
                //If the selected id is not the default one, then hide the drawer. It is default if the user has not selected anything previously and sees the drawer for the first time.
                if (mSelectedMenuId != Constants.GROUP_NONE) {
                    hide();
                }
                break;
        }
        return true;
    }


    private boolean didUserSeeDrawer() {
        return mPref.hasSeenDrawer().getOr(false);
    }

    private void markDrawerSeen() {
        mPref.edit().hasSeenDrawer().put(true).apply();
    }


    void show() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    void hide() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void logout() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
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

    public interface OnGroupSelectedListener {
        void onGroupSelected(String groupId);
    }

}

