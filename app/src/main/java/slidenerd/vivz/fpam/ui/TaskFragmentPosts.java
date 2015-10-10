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
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.GroupMeta;
import slidenerd.vivz.fpam.util.DateUtils;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.PostUtils;

/**
 * The retained fragment used to load posts in the background
 */
@EFragment
public class TaskFragmentPosts extends Fragment {
    @App
    Fpam mApplication;
    TaskCallback mCallback;

    public TaskFragmentPosts() {

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

    void triggerLoadPosts(@NonNull Group group, AccessToken accessToken) {
        if (mCallback != null) {
            mCallback.beforePostsLoaded("Loading posts for the group " + group.getName());
        } else {
            L.m("Callback was null");
        }
        loadPostsAsync(group, accessToken);
    }

    /**
     * If the feed is loaded for the first time, then load posts the simple way without considering the timestamp. There is no need to consider the limit enforced by the admin from the settings of the app since the default option for limit corresponds to 25 which also happens to be the maximum number of results returned by facebook graph api if loading data using the simple way. If the loading process returned non empty results, then store the timestamp of when this group was loaded. If the feed is loaded for a subsequent time, use the previously stored timestamp from the previous step to fetch all the posts from the timestamp till the current time. Since the number of posts may be literally very large, consider the limits set by the admin for settings to get at max limit number of posts.
     *
     * @param group the group selected by the user from the navigation drawer
     * @param token the access token provided by facebook after login
     */
    @Background
    void loadPostsAsync(@NonNull Group group, AccessToken token) {
        if (mApplication.isValidToken()) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                long lastLoadedTimestamp = DataStore.getTimestamp(realm, group);
                ArrayList<Post> posts;
                if (lastLoadedTimestamp == Constants.NA) {
                    posts = FBUtils.requestFeedSync(token, Fpam.getGson(), group);
                } else {
                    posts = FBUtils.requestFeedSince(token, Fpam.getGson(), group, 100, lastLoadedTimestamp);
                }
                DataStore.storePosts(realm, posts);
                long averageInterval = PostUtils.calculateAverageInterval(posts);
                String average = DateUtils.getDurationHHMMSS(averageInterval);
                if (!posts.isEmpty()) {
                    GroupMeta groupMeta = new GroupMeta(group.getId(), System.currentTimeMillis());
                    DataStore.storeGroupMeta(realm, groupMeta);
                }
                onPostsLoaded("Feed loaded with an average activity every " + average + " for ", group);
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
            onPostsLoaded("Did not find a valid access token while loading", group);
        }
    }

    @UiThread
    void onPostsLoaded(String message, Group group) {
        if (mCallback != null) {
            mCallback.afterPostsLoaded(message, group);
        } else {
            L.m("Callback was null");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    interface TaskCallback {
        void beforePostsLoaded(String message);

        void afterPostsLoaded(String message, Group group);
    }
}