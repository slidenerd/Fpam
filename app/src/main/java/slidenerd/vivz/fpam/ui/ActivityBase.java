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
import org.androidannotations.annotations.OptionsMenu;
import org.parceler.Parcels;

import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.DatabaseUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity
@OptionsMenu(R.menu.menu_base)

public abstract class ActivityBase extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TaskFragmentFeed.TaskCallback {

    private static final String STATE_SELECTED_GROUP = "state_selected_group";
    @App
    Fpam mApplication;
    private ProgressDialog mProgress;
    private TaskFragmentFeed_ mTask;
    private FragmentDrawer_ mDrawer;
    private FloatingActionButton mFab;
    private Group mSelectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        if (mApplication.shouldRedirectToLogin()) {
            moveToLogin();
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawer = (FragmentDrawer_) getSupportFragmentManager().findFragmentByTag("nav_drawer");
        if (mDrawer == null) {
            mDrawer = new FragmentDrawer_();
            getSupportFragmentManager().beginTransaction().add(R.id.drawer_container, mDrawer, "nav_drawer").commit();
        }
        mTask = (TaskFragmentFeed_) getSupportFragmentManager().findFragmentByTag("task");
        if (mTask == null) {
            mTask = new TaskFragmentFeed_();
            getSupportFragmentManager().beginTransaction().add(mTask, "task").commit();
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        ViewStub viewStub = (ViewStub) findViewById(R.id.content_main);
        viewStub.setInflatedId(getContentViewRoot());
        viewStub.setLayoutResource(getContentView());
        View mainContentView = viewStub.inflate();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mProgress = new ProgressDialog(this);
        onCreateUserInterface(tabLayout, mainContentView);

        if (savedInstanceState != null) {
            mSelectedGroup = Parcels.unwrap(savedInstanceState.getParcelable(STATE_SELECTED_GROUP));
            if (mSelectedGroup != null) {
                NavUtils.broadcastSelectedGroup(this, mSelectedGroup, false);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SELECTED_GROUP, Parcels.wrap(Group.class, mSelectedGroup));
    }

    private void moveToLogin() {
        NavUtils.startActivityLogin(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem(R.id.action_settings)
    boolean onSettingsSelected() {
        NavUtils.startActivitySettings(this);
        return true;
    }

    @OptionsItem(R.id.action_export)
    boolean onExportDatabaseSelected() {
        DatabaseUtils.exportDatabase(this);
        return true;
    }

    @OptionsItem(R.id.action_cache)
    boolean onCacheSelected() {
        NavUtils.startActivityCache(this);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                onSettingsSelected();
                break;
            case R.id.menu_logout:
                moveToLogin();
                logout();
                break;
            default:
                mSelectedGroup = mDrawer.getSelectedGroup(id);
                if (mSelectedGroup != null) {
                    setTitle(mSelectedGroup.getName());
                    mTask.triggerLoadFeed(mSelectedGroup, mApplication.getToken());
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforeFeedLoaded(String message) {
        mProgress.setTitle("Loading...");
        mProgress.setMessage(message);
        mProgress.show();
    }

    @Override
    public void afterFeedLoaded(String message, Group group) {
        mProgress.dismiss();
        Snackbar.make(mFab, message + " " + group.getName(), Snackbar.LENGTH_LONG)
                .setAction("Yay!", null).show();
        NavUtils.broadcastSelectedGroup(this, group, true);
    }


    private void logout() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
    }


    public abstract int getContentView();

    @IdRes
    public abstract int getContentViewRoot();

    public abstract void onCreateUserInterface(TabLayout tabLayout, View mainContentView);
}
