package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;

import slidenerd.vivz.fpam.R;

@EActivity
@OptionsMenu(R.menu.menu_main)
public class ActivityMain extends ActivityBase {
    private ViewPager mPager;

    @Override
    public boolean hasDrawer() {
        return true;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public int getContentViewRoot() {
        return R.id.main_pager;
    }

    @Override
    public void onCreateUserInterface(TabLayout tabLayout, View mainContentView) {
        mPager = (ViewPager) mainContentView;
        mPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mPager);
    }

    public static class MainPagerAdapter extends FragmentStatePagerAdapter {
        public static final int POSITION_POSTS = 0;
        public static final int TAB_COUNT = 2;
        private Resources mResources;

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
