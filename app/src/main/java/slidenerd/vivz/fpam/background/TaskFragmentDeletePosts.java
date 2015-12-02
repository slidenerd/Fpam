package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.AccessToken;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.util.List;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.core.Core;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Keyword;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_RESPONSE;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_OUTCOME;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POSITION;
import static slidenerd.vivz.fpam.extras.Constants.POST_ID;

/**
 * TODO handle the case where the delete fails or the person has deleted the post from Facebook directly instead of this app
 * The retained fragment used to load posts in the background
 */
@EFragment
public class TaskFragmentDeletePosts extends Fragment {
    @App
    Fpam mApplication;

    @Pref
    MyPrefs_ mPref;
    private Context mContext;

    public TaskFragmentDeletePosts() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    /**
     * @param token the access token provided by facebook after login
     */
    @Background
    void deletePostsAsync(AccessToken token, int position, String postId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Core core = new Core();

            //use the post id to find the post object from the database
            Post post = realm.where(Post.class).equalTo(POST_ID, postId).findFirst();

            //Get the group id of where this post belongs
            String groupId = post.getGroupId();

            //Get the user id of the person who made this post
            String userId = post.getUserId();

            //Get the user name of the person who made this post
            String userName = post.getUserName();


            //If we have a valid post and group id, then issue a request to delete the post
            if (Post.isValidPost(post) && StringUtils.isNotBlank(groupId)) {

                //Trigger delete the post from Facebook
                boolean status = core.deletePost(token, postId);

                //Assume by default that this post was not made by an existing Spammer in our database.
                boolean byExistingSpammer = false;
                if (status) {

                    //If we successfully deleted this post, update the Spammer information accordingly in the database.
                    byExistingSpammer = core.createOrUpdateSpammer(realm, groupId, userId, userName);

                    //Get the list of valid spam keywords or spam phrases applicable on this group id
                    List<Keyword> keywords = core.getKeywordsFor(realm, groupId);

                    //Update the list of top spam keywords or spam phrases for this group id
                    core.updateTopKeywords(realm, groupId, Post.getContent(post), keywords);

                    //Update the list of top spammers for this group id
                    core.updateTopSpammers(realm, groupId);

                    //Trigger the callback to indicate the post was successfully deleted from Facebook
                    onDelete(true, position);

                } else {
                    //Code to be executed if the delete fails
                }

                //Update the analytics information after executing this delete.
                core.updateDailytics(realm, groupId, status, StringUtils.isBlank(post.getMessage()), byExistingSpammer);
            } else {

                //We did not get a valid post or group id to delete and hence trigger the callback to indicate the delete failed
                onDelete(false, position);
            }

        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


    @UiThread
    void onDelete(boolean outcome, int position) {
        Intent intent = new Intent(ACTION_DELETE_RESPONSE);
        intent.putExtra(EXTRA_OUTCOME, outcome);
        intent.putExtra(EXTRA_POSITION, position);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}