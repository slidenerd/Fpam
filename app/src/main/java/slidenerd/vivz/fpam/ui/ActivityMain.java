package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.parceler.Parcels;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskFragmentDeletePosts_;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.util.CopyUtils;

@EActivity
@OptionsMenu(R.menu.menu_main)
public class ActivityMain extends ActivityBase {
    private static final String TAG_FRAGMENT_TASK_DELETE = "delete_posts";
    private ViewPager mPager;
    private TaskFragmentDeletePosts_ mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = (TaskFragmentDeletePosts_) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_TASK_DELETE);
        if (mTask == null) {
            mTask = new TaskFragmentDeletePosts_();
            getSupportFragmentManager().beginTransaction().add(mTask, TAG_FRAGMENT_TASK_DELETE).commit();
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
    public int getContentViewRoot() {
        return R.id.main_pager;
    }

    @Override
    public void onCreateUserInterface(TabLayout tabLayout, View mainContentView) {
        mPager = (ViewPager) mainContentView;
        mPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mPager);
    }

    @Receiver(actions = Constants.ACTION_DELETE_POST, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onBroadcastSwipedPost(Context context, Intent intent) {
        Post swipedPost = Parcels.unwrap(intent.getExtras().getParcelable("post"));
        int position = intent.getExtras().getInt("position");
        L.t(this, "position " + position + " post username " + swipedPost.getUserName());
        mTask.triggerDeletePosts(mApplication.getToken(), position, CopyUtils.duplicatePost(swipedPost));
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
            Fragment fragment = null;
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
