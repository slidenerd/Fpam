package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.core.Core;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.pojo.CollectionPayload;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_ID;
import static slidenerd.vivz.fpam.extras.Constants.KEY_LAST_LOADED_PREFIX;

/**
 * TODO handle the case where the delete fails or the person has deleted the post from Facebook directly instead of this app and try to send a group id instead of the whole group
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

    public void triggerLoadPosts(@NonNull String groupId, AccessToken accessToken) {
        if (mCallback != null) {
            mCallback.beforePostsLoaded();
        }
        loadPostsAsync(groupId, accessToken);
    }

    /**
     * If the feed is loaded for the first time, then load posts the simple way without considering the timestamp. There is no need to consider the limit enforced by the admin from the settings of the app since the default option for limit corresponds to 25 which also happens to be the maximum number of results returned by facebook graph api if loading data using the simple way. If the loading process returned non empty results, then store the timestamp of when this group was loaded. If the feed is loaded for a subsequent time, use the previously stored timestamp from the previous step to fetch all the posts from the timestamp till the current time. Since the number of posts may be literally very large, consider the limits set by the admin for settings to get at max limit number of posts.
     *
     * @param token the access token provided by facebook after login
     */
    @Background
    void loadPostsAsync(@NonNull String groupId, AccessToken token) {
        Realm realm = null;
        SharedPreferences pref;
        FacebookRequestError error = null;
        try {
            if (FBUtils.isValid(token)) {

                realm = Realm.getDefaultInstance();

                Group group = realm.where(Group.class).equalTo(GROUP_ID, groupId).findFirst();

                if (Group.isValidGroup(group)) {

                    pref = PreferenceManager.getDefaultSharedPreferences(mApplication);
                    //Get the maximum number of posts to retrieve or cache size from app settings
                    int maximum = mPref.cacheSize().getOr(Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH);
                    Core core = new Core();

                    //Get the time stamp of when this group was last loaded and convert that timestamp to UTC format
                    long utcTimestamp = pref.getLong(KEY_LAST_LOADED_PREFIX + groupId, 0) / 1000L;
                    ArrayList<Post> posts;

                    CollectionPayload<Post> payload;
                    //If the group was loaded before as indicated by a valid timestamp, then fetch all posts made since that timestamp or maximum number of posts as per the cache size from the app settings whichever is greater

                    if (utcTimestamp > 0) {
                        payload = FBUtils.loadFeedSince(token, Fpam.getGson(), groupId, maximum, utcTimestamp);
                    }

                    //If the group was never loaded before, load it for the first time
                    else {
                        payload = FBUtils.loadFeed(token, Fpam.getGson(), groupId, maximum);
                    }
                    posts = payload.data;
                    error = payload.error;
                    if (!posts.isEmpty()) {
                        //Filter spam posts made by known spammers or containing certain words
                        core.filterPosts(token, realm, groupId, posts);

                        //If we did retrieve posts, update the timestamp of when the group was loaded
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(posts);
                        realm.commitTransaction();

                        pref.edit().putLong(KEY_LAST_LOADED_PREFIX + groupId, System.currentTimeMillis()).commit();
                        //Limit the number of entries stored in the database, based on the cache settings of the app, if the admin has set the cache to 25, if the number of posts loaded were 25 but the number of posts already present in the database were 15, then get rid of the oldest 15 posts and store the new 25 posts in the database.

//                    DataStore.limitStoredPosts(realm, groupId, maximum);
                    }

                }
                //TODO handle invalid group id here
            }
        } catch (JSONException e) {

        } catch (FacebookException e) {

        } finally {
            if (realm != null) {
                realm.close();
            }
            onPostsLoaded(error);
        }
    }

    @UiThread
    void onPostsLoaded(FacebookRequestError error) {
        if (mCallback != null) {
            mCallback.afterPostsLoaded(error);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface TaskCallback {
        void beforePostsLoaded();

        void afterPostsLoaded(FacebookRequestError error);
    }
}