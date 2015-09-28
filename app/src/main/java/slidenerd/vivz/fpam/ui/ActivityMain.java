package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.DatabaseUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity
@OptionsMenu(R.menu.menu_activity_stats)
public class ActivityMain extends ActivityBase {

    private MainPagerAdapter mPagerAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cache) {
            NavUtils.startActivityCache(this);
            return true;
        }
        if (id == R.id.action_settings) {
            NavUtils.startActivitySettings(this);
            return true;
        }
        if (id == R.id.action_test) {

            Intent intent = new Intent(this, TestActivity1.class);
            startActivity(intent);
        }
        if (id == R.id.action_test2) {
            Intent intent = new Intent(this, TestActivity2.class);
            startActivity(intent);
        }
        return false;
    }

    @OptionsItem(R.id.action_export_database)
    void onExportDatabaseSelected() {
        DatabaseUtils.exportDatabase(this);
    }

    @Override
    public int getLayoutForActivity() {
        return R.layout.activity_stats;
    }

    @Override
    public int getRootViewId() {
        return R.id.main_root;
    }

    @Override
    @Nullable
    public int getViewPagerId() {
        return R.id.main_root;
    }

    @Override
    @Nullable
    public PagerAdapter getPagerAdapter() {
        mPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        return mPagerAdapter;
    }


    public class MainPagerAdapter extends FragmentStatePagerAdapter {
        public static final int POSITION_POSTS = 0;
        public static final int POSITION_STATS = 1;
        public static final int TAB_COUNT = 2;
        private Resources mResources;
        private Bundle mArguments;

        public MainPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mResources = context.getResources();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == POSITION_POSTS) {
                fragment = FragmentPosts_.builder().build();
            } else {
                fragment = FragmentStats_.builder().build();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == POSITION_POSTS) {
                return mResources.getString(R.string.tab_posts);
            } else {
                return mResources.getString(R.string.tab_stats);
            }
        }
    }
}