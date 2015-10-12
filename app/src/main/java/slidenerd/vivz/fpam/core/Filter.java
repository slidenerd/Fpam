package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.pojo.DeleteResponseInfo;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

/**
 * Created by vivz on 11/10/15.
 */
public class Filter {
    /**
     * Get the list of posts to filter
     * Get the person id of the guy and group id of the post
     * Check if there exists a spammer with that person id and group id
     * If yes, add the post to the list of posts to be deleted
     * Request delete from facebook API to remove all these posts if we have a non zero list of posts to delete
     * If the outcome is true, delete the posts from facebook and remove them from database
     * Update the spammer data for the deletes that were executed successfully
     *
     * @param token
     * @param realm
     * @param group
     */
    public static String filterPostsOnLoad(AccessToken token, Realm realm, Group group, ArrayList<Post> posts) throws JSONException {

        StringBuffer message = new StringBuffer();

        //find the group id to which this post belongs

        String groupId = group.getId();
        ArrayList<Post> deletePosts = new ArrayList<>(posts.size());
        ArrayList<Spammer> spammers = new ArrayList<>(posts.size());
        int size = 0;
        for (Post post : posts) {

            //find the user id of the person who made this post

            String userId = post.getUserId();

            //the composite primary key of the Spammer table is the combination of user id and group id, so construct the primary key

            String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(userId, groupId);

            //if the post was made by a spammer, add the post to the list of posts to be deleted

            Spammer spammer = getSpammerInfo(realm, compositePrimaryKey, post);
            if (spammer != null) {
                spammers.add(spammer);
                deletePosts.add(post);
                size++;
            }
        }

        //Check if we have posts to delete before requesting deletion to avoid crash

        if (!deletePosts.isEmpty()) {


            //Execute the deletes on all the posts

            ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, deletePosts);

            //Begin a transaction outside the loop to avoid consuming resources while iterating

            realm.beginTransaction();
            for (int i = 0; i < size; i++) {

                DeleteResponseInfo info = infos.get(i);
                Spammer spammer = spammers.get(i);

                //If the post was removed successfully from the Facebook Graph API, remove the corresponding post from realm as well

                if (info.getSuccess()) {

                    //Find the post object whose deletion was successful from Facebook Graph API

                    Post post = info.getPost();

                    //Remove the post from Realm

                    realm.where(Post.class).equalTo("postId", post.getPostId()).findFirst().removeFromRealm();

                    //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                    DataStore.updateSpammerOutsideTransaction(realm, spammer);

                    L.m("Spam successfully removed " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());

                } else {
                    L.m("Delete failed for " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());
                }
            }

            //Commit the transaction outside the loop to avoid consuming resources while iterating

            realm.commitTransaction();
        }
        return message.toString();
    }

    public static void filterPostsOnDelete() {

    }

    public static Spammer getSpammerInfo(Realm realm, String compositePrimaryKey, Post post) {
        return realm.where(Spammer.class).equalTo("userGroupCompositeId", compositePrimaryKey).findFirst();
    }
}
