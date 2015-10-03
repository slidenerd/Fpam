package slidenerd.vivz.fpam.ui;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.facebook.login.LoginManager;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.parceler.Parcels;

import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity
@OptionsMenu(R.menu.main)

public abstract class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TaskFragmentFeed.TaskCallback {

    @App
    Fpam mApplication;
    private TaskFragmentFeed_ mTask;
    private Drawer_ mDrawer;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mApplication.shouldRedirectToLogin()) {
            moveToLogin();
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageBackdrop = (ImageView) findViewById(R.id.app_bar_backdrop);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawer = (Drawer_) getSupportFragmentManager().findFragmentByTag("drawer");
        if (mDrawer == null) {
            mDrawer = new Drawer_();
            getSupportFragmentManager().beginTransaction().add(R.id.drawer_container, mDrawer, "drawer").commit();
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

        onCreateUserInterface(tabLayout, mainContentView);
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
                Group group = mDrawer.getSelectedGroup(id);
                if (group == null) {
                    return false;
                }
                setTitle(group.getName());
                mTask.loadFeed(group, mApplication.getToken());
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFeedLoaded(String message, Group group) {
        Snackbar.make(mFab, message + " " + group.getName(), Snackbar.LENGTH_LONG)
                .setAction("Yay!", null).show();
        notifyFeedLoaded(group);
    }

    private void notifyFeedLoaded(Group group) {
        Intent intent = new Intent("group_selected");
        intent.putExtra("selectedGroup", Parcels.wrap(Group.class, group));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
