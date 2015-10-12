package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.pojo.DeleteResponseInfo;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

/**
 * TODO write the delete module
 * Created by vivz on 11/10/15.
 */
public class Filter {
    /**
     * Get the list of posts to filter
     * Get the person id of the guy and group id of the post
     * Check if there exists a spammer with that person id and group id
     * If yes, add the post to the list of posts to be deleted
     * Request delete from facebook API to remove all these posts
     * If the outcome is true, delete the posts from facebook and remove them from database
     * Update the spammer data for the deletes that were executed successfully
     *
     * @param token
     * @param realm
     * @param group
     */
    public static void filterPostsOnLoad(AccessToken token, Realm realm, Group group, ArrayList<Post> posts) throws JSONException {
        String groupId = group.getId();
        ArrayList<Post> deletePosts = new ArrayList<>(posts.size());
        for (Post post : posts) {
            String userId = post.getUserId();
            String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(userId, groupId);
            if (isPostedBySpammer(realm, compositePrimaryKey, post)) {
                deletePosts.add(post);
            }
        }

        //Check if we have posts to delete before requesting deletion to avoid crash

        if (!deletePosts.isEmpty()) {
            ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, deletePosts);
            realm.beginTransaction();
            for (DeleteResponseInfo info : infos) {
                if (info.getStatus()) {
                    Post post = info.getPost();
                    realm.where(Post.class).equalTo("postId", post.getPostId()).findFirst().removeFromRealm();
                    String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(post.getUserId(), groupId);
                    DataStore.storeOrUpdateSpammerOutsideTransaction(realm, compositePrimaryKey, post.getUserName());
                }
            }
            realm.commitTransaction();
        }

    }

    public static boolean isPostedBySpammer(Realm realm, String compositePrimaryKey, Post post) {
        Spammer spammer = realm.where(Spammer.class).equalTo("userGroupCompositeId", compositePrimaryKey).findFirst();
        return spammer != null;
    }
}
