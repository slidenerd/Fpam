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
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Dailytics;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_ID;
import static slidenerd.vivz.fpam.extras.Constants.POSTLYTICS_ID;

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
        if (FBUtils.isValid(token)) {
            Realm realm = null;
            int originalLoadCount;
            int filteredLoadCount = 0;

            try {
                realm = Realm.getDefaultInstance();

                Group group = realm.where(Group.class).equalTo(GROUP_ID, groupId).findFirst();

                if (Group.isValidGroup(group)) {
                    //Get the maximum number of posts to retrieve or cache size from app settings
                    int maximum = mPref.cacheSize().getOr(Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH);


                    //Get the time stamp of when this group was last loaded and convert that timestamp to UTC format
                    long utcTimestamp = group.getLastLoaded() / 1000L;
                    ArrayList<Post> posts;

                    //If the group was loaded before as indicated by a valid timestamp, then fetch all posts made since that timestamp or maximum number of posts as per the cache size from the app settings whichever is greater

                    if (utcTimestamp > 0) {
                        posts = FBUtils.requestFeedSince(token, Fpam.getGson(), groupId, maximum, utcTimestamp);
                    }

                    //If the group was never loaded before, load it for the first time
                    else {
                        posts = FBUtils.requestFeedFirstTime(token, Fpam.getGson(), groupId, maximum);
                    }
                    originalLoadCount = posts.size();

                    //If we did retrieve posts, update the timestamp of when the group was loaded

                    if (!posts.isEmpty()) {

                        //Filter spam posts made by known spammers or containing certain words

//                    Filter.filterPostsOnLoad(token, realm, groupId, posts);

//                    filteredLoadCount = posts.size();

                        //Compute the unique id of a dailytics object with is the combination of group id and the current date in dd-MM-yyyy format
                        String dailyticsId = Dailytics.computeId(groupId);

                        //Get a reference to the current dailytics object for today
                        Dailytics dailytics = realm.where(Dailytics.class).equalTo(POSTLYTICS_ID, dailyticsId).findFirst();

                        if (dailytics == null) {
                            dailytics = new Dailytics(dailyticsId, 0, 0, 0, 0, 0, 0);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(posts);
                        group.setLastLoaded(System.currentTimeMillis());
                        int scannedOld = dailytics.getScanned();
                        int scannedNew = posts.size();
                        dailytics.setScanned(scannedOld + scannedNew);
                        realm.copyToRealmOrUpdate(dailytics);
                        realm.commitTransaction();
                        //Limit the number of entries stored in the database, based on the cache settings of the app, if the admin has set the cache to 25, if the number of posts loaded were 25 but the number of posts already present in the database were 15, then get rid of the oldest 15 posts and store the new 25 posts in the database.

//                    DataStore.limitStoredPosts(realm, groupId, maximum);
                    }
                } else {
                    L.m("group was invalid while loading posts");
                }
            } catch (JSONException e) {
                L.m("" + e);
            } catch (FacebookException e) {
                L.m("" + e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
                onPostsLoaded();
            }
        } else {
            L.m("invalid access token since it was null or expired while loading posts");
            onPostsLoaded();
        }
    }

    @UiThread
    void onPostsLoaded() {
        if (mCallback != null) {
            mCallback.afterPostsLoaded();
        }
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