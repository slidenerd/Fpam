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
import slidenerd.vivz.fpam.core.AnalyticsManager;
import slidenerd.vivz.fpam.core.Filter;
import slidenerd.vivz.fpam.core.PostlyticsManager;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Analytics;
import slidenerd.vivz.fpam.model.realm.Postlytics;
import slidenerd.vivz.fpam.util.DateUtils;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_ID;

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
        mCallback.beforePostsLoaded();
        loadPostsAsync(groupId, accessToken);
    }

    /**
     * If the feed is loaded for the first time, then load posts the simple way without considering the timestamp. There is no need to consider the limit enforced by the admin from the settings of the app since the default option for limit corresponds to 25 which also happens to be the maximum number of results returned by facebook graph api if loading data using the simple way. If the loading process returned non empty results, then store the timestamp of when this group was loaded. If the feed is loaded for a subsequent time, use the previously stored timestamp from the previous step to fetch all the posts from the timestamp till the current time. Since the number of posts may be literally very large, consider the limits set by the admin for settings to get at max limit number of posts.
     *
     * @param token the access token provided by facebook after login
     */
    @Background
    void loadPostsAsync(@NonNull String groupId, AccessToken token) {
        if (FBUtils.isValid(token)) {
            Realm realm = null;
            int originalLoadCount;
            int filteredLoadCount = 0;

            try {
                realm = Realm.getDefaultInstance();

                //Get the maximum number of posts to retrieve or cache size from app settings

                int maximumPostsStored = mPref.cacheSize().getOr(Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH);

                //Get the time stamp of when this group was last loaded and convert that timestamp to UTC format

                long lastLoadedTimestamp = DateUtils.getUTCTimestamp(DataStore.getLastLoadedTimestamp(realm, groupId));
                ArrayList<Post> posts;

                //If the group was loaded before as indicated by a valid timestamp, then fetch all posts made since that timestamp or maximum number of posts as per the cache size from the app settings whichever is greater

                if (lastLoadedTimestamp > 0) {
                    posts = FBUtils.requestFeedSince(token, Fpam.getGson(), groupId, maximumPostsStored, lastLoadedTimestamp);
                }

                //If the group was never loaded before, load it for the first time
                else {
                    posts = FBUtils.requestFeedFirstTime(token, Fpam.getGson(), groupId);
                }
                originalLoadCount = posts.size();

                //If we did retrieve posts, update the timestamp of when the group was loaded

                if (!posts.isEmpty()) {

                    //Filter spam posts made by known spammers or containing certain words

                    Filter.filterPostsOnLoad(token, realm, groupId, posts);

                    filteredLoadCount = posts.size();

                    Group group = realm.where(Group.class).equalTo(GROUP_ID, groupId).findFirst();
                    //Get hold of the analytics object
                    Analytics analytics = AnalyticsManager.getInstance(realm, groupId, group.getGroupName());

                    //Get a reference to the Postlytics object for the current date in order to update the number of posts scanned
                    Postlytics postlytics = PostlyticsManager.getInstance(realm, groupId);

                    //Get this group object from realm in order to update its timestamp
                    Group realmGroup = realm.where(Group.class).equalTo(GROUP_ID, groupId).findFirst();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(posts);
                    realmGroup.setLastLoaded(System.currentTimeMillis());
                    postlytics.setScanned(postlytics.getScanned() + posts.size());
                    realm.copyToRealmOrUpdate(postlytics);
                    analytics.getEntries().add(postlytics);
                    realm.commitTransaction();
                    //Limit the number of entries stored in the database, based on the cache settings of the app, if the admin has set the cache to 25, if the number of posts loaded were 25 but the number of posts already present in the database were 15, then get rid of the oldest 15 posts and store the new 25 posts in the database.

                    DataStore.limitStoredPosts(realm, groupId, maximumPostsStored);

                }

                String message = null;
                if (originalLoadCount > 0) {
                    message = originalLoadCount + ((originalLoadCount - filteredLoadCount > 0) ? " Posts Loaded And " + (originalLoadCount - filteredLoadCount) + " spam posts removed" : "");

                } else {
                    message = "No New Posts Loaded For " + groupId;
                }
                L.m(message);
                onPostsLoaded();
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
            onPostsLoaded();
        }
    }

    @UiThread
    void onPostsLoaded() {
        mCallback.afterPostsLoaded();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface TaskCallback {
        void beforePostsLoaded();

        void afterPostsLoaded();
    }
}