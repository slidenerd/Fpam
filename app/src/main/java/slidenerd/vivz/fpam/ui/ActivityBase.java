package slidenerd.vivz.fpam.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookRequestError;
import com.facebook.login.LoginManager;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.lang3.StringUtils;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskFragmentLoadPosts;
import slidenerd.vivz.fpam.background.TaskFragmentLoadPosts_;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Dailytics;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.model.realm.TopKeyword;
import slidenerd.vivz.fpam.settings.SettingsActivity_;
import slidenerd.vivz.fpam.util.DatabaseUtils;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_LOAD_FEED;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_SELECTED_GROUP;
import static slidenerd.vivz.fpam.extras.Constants.PERIOD;
import static slidenerd.vivz.fpam.extras.Constants.TAG;

@EActivity

public abstract class ActivityBase extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TaskFragmentLoadPosts.TaskCallback {

    private static final String TAG_DRAWER = "nav_drawer";
    private static final String TAG_TASK = "task_load_posts";
    @Pref
    public MyPrefs_ mPref;
    @App
    protected Fpam mApp;
    private TaskFragmentLoadPosts_ mTask;
    private FragmentDrawer_ mDrawer;
    private FloatingActionButton mFab;
    private String mGroupId;
    private String mTitle;
    private DrawerLayout mDrawerLayout;
    private AccessTokenTracker mTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            AccessToken.setCurrentAccessToken(currentAccessToken);
            mApp.setToken(currentAccessToken);
            Log.i(TAG, "onCurrentAccessTokenChanged: " + oldAccessToken + " " + currentAccessToken);
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.key_scan_frequency))) {
                Toast.makeText(ActivityBase.this, "Changed " + sharedPreferences.getInt(getString(R.string.key_scan_frequency), -1), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        //If the access token has expired or null, take the user back to the login screen, and call return in addition to finish() of the activity to halt processing any remaining code inside onCreate

        if (!FBUtils.canRead(mApp.getToken())) {
            moveToLogin();
            return;
        }

        //Initialize the Floating Action Button

        initFab();

        //Initialize our retained fragment that performs the task of loading posts in the background

        initTask();

        //Initialize our Drawer Fragment that contains the navigation view and adds admin information and groups information

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (hasDrawer()) {
            mTitle = mPref.lastLoadedTitle().get();
            mGroupId = mPref.lastLoadedGroup().get();
            getSupportActionBar().setTitle(StringUtils.isNoneBlank(mTitle, mGroupId) ? mTitle : getString(R.string.app_name));
            initDrawer(toolbar);
        }

        //Inflate the child class root View which is represented by a ViewStub in this layout

        ViewStub viewStub = (ViewStub) findViewById(R.id.content_main);
        viewStub.setInflatedId(getRoot());
        viewStub.setLayoutResource(getContentView());
        View mainContentView = viewStub.inflate();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        Intent intent = new Intent("slidenerd.vivz.fpam.START_SCAN_RECEIVER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Give the subclasses an opportunity to create their views by indicating the parent tablayout and their main content view is ready.
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + PERIOD, PERIOD, pendingIntent);
        init(tabLayout, mainContentView);

    }


    private void broadcastSelectedGroup(String groupId) {
        Intent intent = new Intent(ACTION_LOAD_FEED);
        intent.putExtra(EXTRA_SELECTED_GROUP, groupId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void moveToLogin() {
        ActivityLogin_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        finish();
    }

    private void initFab() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initTask() {
        mTask = (TaskFragmentLoadPosts_) getSupportFragmentManager().findFragmentByTag(TAG_TASK);
        if (mTask == null) {
            mTask = new TaskFragmentLoadPosts_();
            getSupportFragmentManager().beginTransaction().add(mTask, TAG_TASK).commit();
        }
    }

    private void initDrawer(Toolbar toolbar) {

        mDrawer = (FragmentDrawer_) getSupportFragmentManager().findFragmentByTag(TAG_DRAWER);
        if (mDrawer == null) {
            mDrawer = new FragmentDrawer_();
            getSupportFragmentManager().beginTransaction().add(R.id.drawer_container, mDrawer, TAG_DRAWER).commit();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPref.getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPref.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
    }

    /**
     * If the drawer is open, close the drawer, else let the default behavior take place for back press
     */
    @Override
    public void onBackPressed() {
        if (hasDrawer() && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem(R.id.action_settings)
    protected boolean onSettingsSelected() {
        if (hasDrawer() && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        SettingsActivity_.intent(this).start();
        return true;
    }

    @OptionsItem(R.id.action_export)
    protected boolean onExportDatabaseSelected() {
        DatabaseUtils.exportDatabase(this);
        return true;
    }

    @OptionsItem(R.id.action_cache)
    protected boolean onCacheSelected() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(TopKeyword.class).findAll().clear();
        realm.where(Dailytics.class).findAll().clear();
        realm.where(Spammer.class).findAll().clear();
        realm.where(Post.class).findAll().clear();
        realm.where(Keyword.class).findAll().clear();
        realm.commitTransaction();
        realm.close();
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view data clicks here.
        int id = item.getItemId();
        mGroupId = mDrawer.getSelected(id);
        if (mGroupId != null) {
            mTitle = (String) item.getTitle();
            getSupportActionBar().setTitle(mTitle);
            mPref.lastLoadedGroup().put(mGroupId);
            mPref.lastLoadedTitle().put(mTitle);
            mTask.triggerLoadPosts(mGroupId, mApp.getToken());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforePostsLoaded() {
        FragmentProgress.show(this);
    }

    @Override
    public void afterPostsLoaded(FacebookRequestError error) {
        FragmentProgress.dismiss(this);
        broadcastSelectedGroup(mGroupId);
    }


    public void logout() {
        moveToLogin();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTracker.stopTracking();
    }

    public abstract boolean hasDrawer();

    /**
     * @return the layout id of the child activity
     */
    public abstract int getContentView();

    /**
     * @return the id of the root View of the layout of the child
     */
    @IdRes
    public abstract int getRoot();

    /**
     * @param tabLayout       the tab layout of the parent which can be used to link with the ViewPager in the child if it uses ViewPager as its root
     * @param mainContentView the View object corresponding to the root View of the child
     */

    public abstract void init(TabLayout tabLayout, View mainContentView);
}
