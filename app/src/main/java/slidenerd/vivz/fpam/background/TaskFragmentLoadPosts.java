package slidenerd.vivz.fpam.background;

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
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.core.Filter;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.GroupMeta;
import slidenerd.vivz.fpam.prefs.MyPrefs_;
import slidenerd.vivz.fpam.util.DateUtils;
import slidenerd.vivz.fpam.util.FBUtils;

/**
 * The retained fragment used to load posts in the background
 */
@EFragment
public class TaskFragmentLoadPosts extends Fragment {
    @App
    Fpam mApplication;
    TaskCallback mCallback;

    @Pref
    MyPrefs_ mPref;

    public TaskFragmentLoadPosts() {

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

    public void triggerLoadPosts(@NonNull Group group, AccessToken accessToken) {
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

                //Get the maximum number of posts to retrieve or cache size from app settings

                int maximumPostsStored = mPref.cacheSize().getOr(Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH);

                //Get the time stamp of when this group was last loaded and convert that timestamp to UTC format

                long lastLoadedTimestamp = DateUtils.getUTCTimestamp(DataStore.getTimestamp(realm, group));
                ArrayList<Post> posts;

                //If the group was loaded before as indicated by a valid timestamp, then fetch all posts made since that timestamp or maximum number of posts as per the cache size from the app settings whichever is greater

                if (lastLoadedTimestamp > 0) {
                    posts = FBUtils.requestFeedSince(token, Fpam.getGson(), group, maximumPostsStored, lastLoadedTimestamp);
                    onProgressUpdate("Loaded", posts.size() + " posts from " + group.getName());
                }

                //If the group was never loaded before, load it for the first time

                else {
                    posts = FBUtils.requestFeedFirstTime(token, Fpam.getGson(), group);
                    onProgressUpdate("Loaded", posts.size() + " posts from " + group.getName());
                }
                DataStore.storePosts(realm, posts);

                //If we did retrieve posts, update the timestamp of when the group was loaded

                if (!posts.isEmpty()) {

                    //update the timestamp and the metadata of the group that was just loaded

                    GroupMeta groupMeta = new GroupMeta(group.getId(), System.currentTimeMillis());
                    DataStore.storeGroupMeta(realm, groupMeta);

                    //Limit the number of entries stored in the database, based on the cache settings of the app, if the admin has set the cache to 25, if the number of posts loaded were 25 but the number of posts already present in the database were 15, then get rid of the oldest 15 posts and store the new 25 posts in the database.

                    DataStore.limitStoredPosts(realm, group, maximumPostsStored);

                    //Filter spam posts made by known spammers or containing certain words
                    Filter.filterPostsOnLoad(token, realm, group, posts);
                }

                onPostsLoaded(posts.size() + " posts loaded for ", group);
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
    void onProgressUpdate(String title, String message) {
        if (mCallback != null) {
            mCallback.onProgressUpdate(title, message);
        } else {
            L.m("Callback was null");
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

    public interface TaskCallback {
        void beforePostsLoaded(String message);

        void onProgressUpdate(String title, String message);

        void afterPostsLoaded(String message, Group group);
    }
}