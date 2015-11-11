package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.pojo.DeleteRequestInfo;
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

        String groupId = group.getGroupId();
        ArrayList<DeleteRequestInfo> deletes = new ArrayList<>(posts.size());
        ArrayList<Spammer> spammers = new ArrayList<>(posts.size());
        int size = 0;
        for (Post post : posts) {

            //find the user id of the person who made this post

            String userId = post.getUserId();

            if (userId != null && !userId.trim().isEmpty()) {
                //Is the user id of the person making this post in the list of spammmers regardless of the group where the spammer made the post?

                Spammer spammer = realm.where(Spammer.class).beginsWith("compositeUserGroupId", userId).findFirst();

                //if the post was made by a spammer, add the post to the list of posts to be deleted

                if (spammer != null) {
                    spammers.add(spammer);
                    deletes.add(new DeleteRequestInfo(0, post));
                    size++;
                }
            }
        }

        //Check if we have posts to delete before requesting deletion to avoid crash

        if (!deletes.isEmpty()) {


            //Execute the deletes on all the posts

            ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, deletes);

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

                    //Remove the post from the Collection

                    L.m("Spam post successfully removed " + post.getPostId() + " made by " + post.getUserName());
                    posts.remove(post);

                    //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                    String compositeUserGroupId = ModelUtils.computeSpammerId(userId, groupId);

                    //if there is a spammer entry with the user id of the person making this post and the group id of this group

                    if (compositeUserGroupId.contains(groupId)) {

                        //this spammer has spammed in this group before, increment the number of spam posts made by this spammer and update the timestamp

                        spammer.setSpamCount(spammer.getSpamCount() + 1);
                        spammer.setLastActive(System.currentTimeMillis());

                        L.m("updating existing spammer " + spammer.getUserName());

                    } else {

                        //this spammer hasnt spammed in this group before

                        Spammer newSpammer = new Spammer(compositeUserGroupId, spammer.getUserName(), 1, System.currentTimeMillis(), false);
                        realm.copyToRealmOrUpdate(newSpammer);
                        L.m("adding new spammer " + spammer.getUserName());
                    }

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
}
