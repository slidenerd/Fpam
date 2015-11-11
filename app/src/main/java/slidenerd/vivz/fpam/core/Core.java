package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

/**
 * TODO complete this class
 * Created by vivz on 10/11/15.
 */
public class Core {
    public List<Keyword> getRelevantKeywords(Realm realm, String groupId) {
        RealmResults<Keyword> keywords = realm.where(Keyword.class).findAll();
        RealmList<Keyword> list = new RealmList<>();
        for (Keyword keyword : keywords) {

            //get the list of all groups applicable on this keyword
            RealmList<Group> groups = keyword.getGroups();

            //Since we haven't specified the list of groups to which this keyword applies, it means this keyword applies to all groups without any preference, hence add this keyword to the list of retrieved keywords
            if (groups == null || groups.isEmpty()) {
                list.add(keyword);
            } else {

                //for each applicable group of the current keyword, check if its group id matches with our target, if yes add this keyword to the list to be retrieved
                for (Group group : groups) {
                    if (group.getGroupId().equals(groupId)) {
                        list.add(keyword);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public boolean deletePost(int position, AccessToken token, Group group, Post post, Realm realm) throws JSONException {
        List<Keyword> keywords = getRelevantKeywords(realm, group.getGroupId());

        boolean success = FBUtils.requestDeletePost(token, post);
        if (success) {
            String userId = post.getUserId();
            Spammer spammer = realm.where(Spammer.class).beginsWith("compositeUserGroupId", userId).findFirst();

            //Check if only the user id exists in the database
            String compositeUserGroupId = ModelUtils.computeSpammerId(userId, group.getGroupId());

            //if the user id does not exist in the database, add the combination of this user id and group id as a new entry in the Spammers database
            if (spammer == null) {
                spammer = new Spammer(compositeUserGroupId, post.getUserName(), 1, System.currentTimeMillis(), false);
                realm.beginTransaction();
                realm.copyToRealm(spammer);
                realm.commitTransaction();
            }

            //if the user id exists in the database, we may or may not have a combination of this user id and group id in the database
            else {

                spammer = realm.where(Spammer.class).equalTo("compositeUserGroupId", compositeUserGroupId).findFirst();

                //if the combination of user id and group id is not found in the database, create it
                if (spammer == null) {
                    spammer = new Spammer(compositeUserGroupId, post.getUserName(), 1, System.currentTimeMillis(), false);
                }

                //if the combination of user id and group id is found in the database, increment the number of spam posts made by that spammer and update the last active time of the spammer.
                else {
                    int count = spammer.getSpamCount();
                    long lastActive = spammer.getLastActive();
                    realm.beginTransaction();
                    spammer.setSpamCount(count + 1);
                    spammer.setLastActive(post.getUpdatedTime());
                    realm.commitTransaction();
                }
            }


            //Get the message, caption and description of the post and concatenate them as content for further processing purposes.
            String message = post.getMessage();
            String caption = post.getCaption();
            String description = post.getDescription();
            String content = message + "\n" + caption + "\n" + description;

            if (message == null || message.trim().isEmpty()) {
                //update the number of empty messages found under this group id as part of analytics
            }

            if (content != null && !content.trim().isEmpty()) {
                for (Keyword keyword : keywords) {
                    if (content.toLowerCase().contains(keyword.getKeyword())) {
                        //update the number of times each keyword is found as part of the analytics
                    }
                }
            }


            //update top x spammers as part of analytics

            //update top x keywords as part of analytics

        } else {
            //update delete failed count
        }
        return success;
    }


}
