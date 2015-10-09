package slidenerd.vivz.fpam.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.FacebookException;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.PostUtils;

@EFragment
public class TaskFragmentFeed extends Fragment {
    @App
    Fpam mApplication;
    TaskCallback mCallback;

    public TaskFragmentFeed() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (TaskCallback) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (TaskCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    void triggerLoadFeed(@NonNull Group group, AccessToken accessToken) {
        if (mCallback != null) {
            mCallback.beforeFeedLoaded("Loading posts for the group " + group.getName());
        }
        loadFeedInBackground(group, accessToken);
    }

    @Background
    void loadFeedInBackground(@NonNull Group group, AccessToken accessToken) {
        if (mApplication.hasToken()) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                ArrayList<Post> listPosts = FBUtils.requestFeedSince(accessToken, Fpam.getGson(), group, 10, 1444372298);
                DataStore.storeFeed(realm, listPosts);
                PostUtils.sortByCreatedTime(listPosts);
                ArrayList<Post> listPostsLast24Hours = PostUtils.getPostsLast24Hours(listPosts);
                String postingFrequency = PostUtils.calculatePostingFrequency(listPostsLast24Hours);
                onFeedLoaded("FeedFields Loaded With Frequency " + postingFrequency + " for", group);
            } catch (JSONException e) {
                L.m("" + e);
            } catch (FacebookException e) {
                L.m("" + e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        } else {
            onFeedLoaded("Did not find a valid access token while loading", group);
        }
    }

    @UiThread
    void onFeedLoaded(String message, Group group) {
        if (mCallback != null) {
            mCallback.afterFeedLoaded(message, group);
        } else {
            L.m("callback was null");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    interface TaskCallback {
        void beforeFeedLoaded(String message);

        void afterFeedLoaded(String message, Group group);
    }
}