package slidenerd.vivz.fpam.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.facebook.login.LoginManager;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.parceler.Parcels;

import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskFragmentLoadPosts;
import slidenerd.vivz.fpam.background.TaskFragmentLoadPosts_;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.DatabaseUtils;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity

public abstract class ActivityBase extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TaskFragmentLoadPosts.TaskCallback {

    private static final String STATE_SELECTED_GROUP = "state_selected_group";
    private static final String TAG_FRAGMENT_DRAWER = "nav_drawer";
    private static final String TAG_FRAGMENT_TASK_POSTS = "task_fragment";
    @App
    protected Fpam mApplication;
    private ProgressDialog mProgress;
    private TaskFragmentLoadPosts_ mTask;
    private FragmentDrawer_ mDrawer;
    private FloatingActionButton mFab;
    private Group mSelectedGroup;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //If the access token has expired or null, take the user back to the login screen, and call return in addition to finish() of the activity to halt processing any remaining code inside onCreate

        if (!FBUtils.isValid(mApplication.getToken())) {
            moveToLogin();
            return;
        }

        //Initialize the Floating Action Button

        initFab();

        //Initialize our retained fragment that performs the task of loading posts in the background

        initTaskFragment();

        //Initialize our Drawer Fragment that contains the navigation view and adds admin information and groups information

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (hasDrawer()) {
            initNavigationDrawer(toolbar);
        }

        //Inflate the child class root View which is represented by a ViewStub in this layout

        ViewStub viewStub = (ViewStub) findViewById(R.id.content_main);
        viewStub.setInflatedId(getContentViewRoot());
        viewStub.setLayoutResource(getContentView());
        View mainContentView = viewStub.inflate();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mProgress = new ProgressDialog(this);

        //Give the subclasses an opportunity to create their views by indicating the parent tablayout and their main content view is ready.

        onCreateUserInterface(tabLayout, mainContentView);

        if (savedInstanceState != null) {
            mSelectedGroup = Parcels.unwrap(savedInstanceState.getParcelable(STATE_SELECTED_GROUP));
            if (mSelectedGroup != null) {
                NavUtils.broadcastSelectedGroup(this, mSelectedGroup, false);
            }
        }
    }

    private void moveToLogin() {
        NavUtils.startActivityLogin(this);
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

    private void initTaskFragment() {
        mTask = (TaskFragmentLoadPosts_) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_TASK_POSTS);
        if (mTask == null) {
            mTask = new TaskFragmentLoadPosts_();
            getSupportFragmentManager().beginTransaction().add(mTask, TAG_FRAGMENT_TASK_POSTS).commit();
        }
    }

    private void initNavigationDrawer(Toolbar toolbar) {

        mDrawer = (FragmentDrawer_) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DRAWER);
        if (mDrawer == null) {
            mDrawer = new FragmentDrawer_();
            getSupportFragmentManager().beginTransaction().add(R.id.drawer_container, mDrawer, TAG_FRAGMENT_DRAWER).commit();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SELECTED_GROUP, Parcels.wrap(Group.class, mSelectedGroup));
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
        NavUtils.startActivitySettings(this);
        return true;
    }

    @OptionsItem(R.id.action_export)
    protected boolean onExportDatabaseSelected() {
        DatabaseUtils.exportDatabase(this);
        return true;
    }

    @OptionsItem(R.id.action_cache)
    protected boolean onCacheSelected() {
        NavUtils.startActivityCache(this);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mSelectedGroup = mDrawer.getSelectedGroup(id);
        if (mSelectedGroup != null) {
            setTitle(mSelectedGroup.getName());
            mTask.triggerLoadPosts(mSelectedGroup, mApplication.getToken());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforePostsLoaded(String message) {
        mProgress.setTitle("Loading...");
        mProgress.setMessage(message);
        mProgress.show();
    }

    @Override
    public void onProgressUpdate(String title, String message) {
        mProgress.setTitle(title);
        mProgress.setMessage(message);
    }

    @Override
    public void afterPostsLoaded(String message, Group group) {
        mProgress.dismiss();
        Snackbar.make(mFab, message, Snackbar.LENGTH_LONG)
                .setAction("Yay!", null).show();
        NavUtils.broadcastSelectedGroup(this, group, true);
    }


    public void logout() {
        moveToLogin();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
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
    public abstract int getContentViewRoot();

    /**
     * @param tabLayout       the tab layout of the parent which can be used to link with the ViewPager in the child if it uses ViewPager as its root
     * @param mainContentView the View object corresponding to the root View of the child
     */

    public abstract void onCreateUserInterface(TabLayout tabLayout, View mainContentView);
}
