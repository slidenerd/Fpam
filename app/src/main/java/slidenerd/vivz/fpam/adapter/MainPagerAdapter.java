package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.ui.FragmentPosts;
import slidenerd.vivz.fpam.ui.FragmentStats;

/**
 * Created by vivz on 25/09/15.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
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
            fragment = new FragmentPosts();
        } else {
            fragment = new FragmentStats();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mResources.getString(R.string.tab_posts);
        } else {
            return mResources.getString(R.string.tab_stats);
        }
    }
}
