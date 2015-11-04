package slidenerd.vivz.fpam.background;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.pojo.DeleteRequestInfo;
import slidenerd.vivz.fpam.model.pojo.DeleteResponseInfo;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

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

    public TaskFragmentDeletePosts() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void triggerDeletePosts(AccessToken token, int position, Post post) {
        deletePostsAsync(token, position, post);
    }

    /**
     * @param token the access token provided by facebook after login
     */
    @Background
    void deletePostsAsync(AccessToken token, int position, Post post) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            String postId = post.getPostId();
            String groupId = postId.substring(0, postId.indexOf("_"));
            Group group = realm.where(Group.class).equalTo("groupId", groupId).findFirst();
            RealmResults<Post> results = realm.where(Post.class).beginsWith("postId", group.getGroupId()).findAllSorted("updatedTime", false);
            //realm.where(Post.class).equalTo("userId", post.getUserId()).findAll();
            ArrayList<DeleteRequestInfo> deletes = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                Post current = results.get(i);
                if (current.getUserId() != null && current.getUserId().equals(post.getUserId())) {
                    DeleteRequestInfo info = new DeleteRequestInfo(i, current);
                    deletes.add(info);
                }
            }
            if (!deletes.isEmpty()) {
                ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, deletes);

                String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(post.getUserId(), group.getGroupId());

                realm.beginTransaction();
                int numberOfPostsDeleted = 0;
                for (DeleteResponseInfo info : infos) {

                    //If the post was removed successfully from the Facebook Graph API, remove the corresponding post from realm as well

                    if (info.getSuccess()) {

                        //Remove the post from Realm
                        realm.where(Post.class).equalTo("postId", info.getPost().getPostId()).findFirst().removeFromRealm();

                        numberOfPostsDeleted++;

                    } else {
                        L.m("Delete failed for " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());
                    }
                }

                //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted


                realm.commitTransaction();

                //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                if (numberOfPostsDeleted > 0) {
                    L.m("Delete successful by " + post.getUserName());
                    DataStore.storeOrUpdateSpammer(realm, compositePrimaryKey, post.getUserId(), group.getGroupId(), post.getUserName(), numberOfPostsDeleted);
                }
                onPostsDeleted(numberOfPostsDeleted > 0);
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
    void onPostsDeleted(boolean outcome) {
        L.t(getActivity(), "outcome " + outcome);
    }
}