package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskFragmentDeletePosts_;
import slidenerd.vivz.fpam.extras.Constants;

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
        Fragment[] fragments = {FragmentPosts_.builder().build(), FragmentStats_.builder().build()};
        String[] titles = getResources().getStringArray(R.array.tabs);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments, titles);
        adapter.setLocked(true, 0);
        mPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mPager);
    }

    @Receiver(actions = Constants.ACTION_DELETE_POST, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onDeleteRequest(Context context, Intent intent) {
        String postId = intent.getExtras().getString(EXTRA_ID);
        int position = intent.getExtras().getInt(EXTRA_POSITION);
        Log.i(TAG, "onDeleteRequest: " + postId + " pos " + position);
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
        private Fragment[] fragments;
        private String[] titles;
        private boolean locked = false;
        private int lockedIndex;

        public MainPagerAdapter(FragmentManager fm, Fragment[] fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        /**
         * @param locked Boolean indicating that the ViewPager is locked or disabled so that the Adapter displays only one page as per the parameter below.
         * @param page   The index of the item inside the Adapter that should be displayed always after swiping the ViewPager with finger has been disabled.
         */
        public void setLocked(boolean locked, int page) {
            this.locked = locked;
            lockedIndex = page;
            notifyDataSetChanged();
        }


        /**
         * Return the number of views available.
         * If we have locked the Adapter, then return 1 to indicate we only want a single page to be displayed.
         */
        @Override
        public int getCount() {
            if (locked) return 1;
            return fragments.length;
        }

        /**
         * This method may be called by the ViewPager to obtain a title string to describe the specified page.
         * This method may return null indicating no title for this page. The default implementation returns null.
         * If we have locked the Adapter, then return the title of the item from the locked position of the Adapter.
         *
         * @param position Position for which we need the title.
         * @return String containing the title to be displayed at the specified position
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (locked) return titles[lockedIndex];
            return titles[position];
        }

        /**
         * Return the Fragment associated with a specified position.
         * If we have locked the Adapter, then return the item from the locked position of the Adapter
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            if (locked) return fragments[lockedIndex];
            return fragments[position];
        }
    }
}
