package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.androidannotations.annotations.EActivity;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;

@EActivity
public class Child extends Main {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.m("this got called");
    }

    @Override
    public int getContentView() {
        return R.layout.content_child;
    }

    @Override
    public int getContentViewRoot() {
        return R.id.main_pager;
    }

    @Override
    public void onCreateUserInterface(TabLayout tabLayout, View mainContentView) {
        ViewPager viewPager = (ViewPager) mainContentView;
        viewPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class MainPagerAdapter extends FragmentStatePagerAdapter {
        public static final int POSITION_POSTS = 0;
        public static final int POSITION_STATS = 1;
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
