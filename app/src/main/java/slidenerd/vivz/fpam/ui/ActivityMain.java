package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskFragmentDeletePosts_;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.L;

import static slidenerd.vivz.fpam.extras.Constants.EXTRA_ID;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POSITION;

@EActivity
@OptionsMenu(R.menu.menu_main)
public class ActivityMain extends ActivityBase {
    private static final String TAG = "delete_posts";
    private ViewPager mPager;
    private TaskFragmentDeletePosts_ mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = (TaskFragmentDeletePosts_) getSupportFragmentManager().findFragmentByTag(TAG);
        if (mTask == null) {
            mTask = new TaskFragmentDeletePosts_();
            getSupportFragmentManager().beginTransaction().add(mTask, TAG).commit();
        }
    }

    @Override
    public boolean hasDrawer() {
        return true;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public int getRoot() {
        return R.id.main_pager;
    }

    @Override
    public void init(TabLayout tabLayout, View mainContentView) {
        mPager = (ViewPager) mainContentView;
        mPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mPager);
    }

    @Receiver(actions = Constants.ACTION_DELETE_POST, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onDeleteRequest(Context context, Intent intent) {
        String postId = intent.getExtras().getString(EXTRA_ID);
        int position = intent.getExtras().getInt(EXTRA_POSITION);
        L.m("position " + position + " post id " + postId);
        if (postId != null) {
            mTask.deletePostsAsync(mApp.getToken(), position, postId);
        } else {
            final Snackbar snackbar = Snackbar.make(mPager, R.string.message_invalid_post, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    public static class MainPagerAdapter extends FragmentStatePagerAdapter {
        public static final int TAB_COUNT = 2;
        private Resources mResources;

        public MainPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mResources = context.getResources();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == 0) {
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
            if (position == 0) {
                return mResources.getString(R.string.tab_posts);
            } else {
                return mResources.getString(R.string.tab_stats);
            }
        }
    }
}
