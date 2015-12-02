package slidenerd.vivz.fpam.settings;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.ui.ActivityBase;

@EActivity
public class SettingsActivity extends ActivityBase {


    private ViewPager mPager;

    @OptionsItem(android.R.id.home)
    void onHomeSelected() {
        onBackPressed();
    }

    @Override
    public boolean hasDrawer() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_settings;
    }

    @Override
    public int getRoot() {
        return R.id.content_main;
    }

    @Override
    public void init(TabLayout tabLayout, View mainContentView) {
        mPager = (ViewPager) mainContentView;
        mPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mPager);
    }

    public static class MainPagerAdapter extends FragmentStatePagerAdapter {
        public static final int TAB_COUNT = 2;
        private final Resources mResources;

        public MainPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mResources = context.getResources();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return SettingsFragmentGeneral_.builder().build();
            } else {
                return SettingsFragmentGroups_.builder().build();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return mResources.getString(R.string.tab_general);
            } else {
                return mResources.getString(R.string.tab_groups);
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}
