package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
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

            //if the post was made by a spammer, add the post to the list of posts to be deleted

            Spammer spammer = realm.where(Spammer.class).beginsWith("userGroupCompositeId", userId).findFirst();
            if (spammer != null) {
                spammers.add(spammer);
                deletePosts.add(post);
                size++;
            } else {
                L.m("Spammer was null");
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

                    String userId = post.getUserId();

                    //Remove the post from Realm

                    realm.where(Post.class).equalTo("postId", post.getPostId()).findFirst().removeFromRealm();

                    //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                    String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(userId, groupId);
                    if (compositePrimaryKey.contains(groupId)) {

                        //this spammer has spammed in this group before

                        spammer.setSpamCount(spammer.getSpamCount() + 1);
                        spammer.setTimestamp(System.currentTimeMillis());

                    } else {

                        //this spammer hasnt spammed in this group before

                        Spammer newSpammer = new Spammer(compositePrimaryKey, spammer.getUserName(), 1, System.currentTimeMillis());
                        realm.copyToRealmOrUpdate(newSpammer);
                    }


                    L.m("Spam successfully removed " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());

                } else {
                    L.m("Delete failed for " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());
                }
            }

            //Commit the transaction outside the loop to avoid consuming resources while iterating

            realm.commitTransaction();
        } else {
            L.m("to delete list was empty");
        }
        return message.toString();
    }

    public static int filterPostsOnDelete(AccessToken token, Realm realm, Group group, Post post) throws JSONException {
        int numberOfPostsDeleted = 0;
        RealmResults<Post> results = realm.where(Post.class).equalTo("userId", post.getUserId()).findAll();
        if (!results.isEmpty()) {
            ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, results);

            String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(post.getUserId(), group.getId());

            realm.beginTransaction();
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
                DataStore.storeOrUpdateSpammer(realm, compositePrimaryKey, post.getUserName(), numberOfPostsDeleted);
            }
        }
        return numberOfPostsDeleted;
    }
}
