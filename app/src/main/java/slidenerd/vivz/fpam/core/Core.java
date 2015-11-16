package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.json.JSONException;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Analytics;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Postlytics;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_ID;
import static slidenerd.vivz.fpam.extras.Constants.POST_ID;

/**
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

    public boolean deletePostFB(int position, AccessToken token, String postId, Realm realm) throws JSONException {
        Post post = realm.where(Post.class).equalTo(POST_ID, postId).findFirst();
        List<Keyword> keywords = getRelevantKeywords(realm, post.getGroupId());
        Group group = realm.where(Group.class).equalTo(GROUP_ID, post.getGroupId()).findFirst();
        Analytics analytics = AnalyticsManager.getInstance(realm, post.getGroupId(), group.getGroupName());
        boolean success = FBUtils.requestDeletePost(token, post);
        String spammerId = ModelUtils.computeSpammerId(post.getUserId(), post.getGroupId());
        Postlytics postlytics = PostlyticsManager.getInstance(realm, post.getGroupId());
        int deleted = 0;
        int deletedEmpty = 0;
        int deletedKeywords = 0;
        int deletedSpammer = 0;
        int failed = 0;
        if (success) {

            deleted++;

            //if the user id does not exist in the database, add the combination of this user id and group id as a new entry in the Spammers database

            Spammer spammer = SpammerManager.getSpammer(realm, post.getUserId());
            if (spammer == null) {
                spammer = new Spammer(spammerId, post.getUserName(), 1, System.currentTimeMillis(), false);
                realm.beginTransaction();
                realm.copyToRealm(spammer);
                realm.commitTransaction();
            }

            //if the user id exists in the database, we may or may not have a combination of this user id and group id in the database
            else {

                spammer = SpammerManager.getInstance(realm, spammerId);
                realm.beginTransaction();
                spammer.setUserName(post.getUserName());
                spammer.setSpamCount(spammer.getSpamCount() + 1);
                spammer.setLastActive(System.currentTimeMillis());
                realm.commitTransaction();
                deletedSpammer++;
                //if the combination of user id and group id is not found in the database, create it
            }


            //Get the message, caption and description of the post and concatenate them as content for further processing purposes.
            String message = post.getMessage();
            String caption = post.getCaption();
            String description = post.getDescription();
            StringBuffer content = new StringBuffer();
            if (message != null) {
                content.append(message.toLowerCase()).append(" ");
            }
            if (caption != null) {
                content.append(caption.toLowerCase()).append(" ");
            }
            if (description != null) {
                content.append(description.toLowerCase());
            }

            if (message == null || message.trim().isEmpty()) {
                //update the number of empty messages found under this group id as part of analytics
//                PostlyticsManager.updateEmpty(postlytics, realm, 1);
                deletedEmpty++;
            }
//            PostlyticsManager.updateDeleted(postlytics, realm, 1);
            if (!content.toString().trim().isEmpty()) {
                Keyword keyword = null;
                for (int i = 0; i < keywords.size(); i++) {
                    int count = 0;
                    keyword = keywords.get(i);
                    if (content.toString().contains(keyword.getKeyword())) {
                        //update the number of times each keyword is found as part of the analytics
                        count++;
                    }
                    if (keyword != null) {
                        RealmList<Group> groups = keyword.getGroups();
                        for (Group g : groups) {
                            if (g.getGroupId().equals(post.getGroupId())) {
                                realm.beginTransaction();
                                g.setCount(g.getCount() + count);
                                realm.commitTransaction();
                                break;
                            }
                        }
                    }
                }
            }


            //update top x spammers as part of analytics

            //update top x keywords as part of analytics

        } else {
            //update delete failed count
            PostlyticsManager.updateFailed(postlytics, realm, 1);
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(postlytics);
        analytics.getEntries().add(postlytics);
        realm.copyToRealmOrUpdate(analytics);
        realm.commitTransaction();
        return success;
    }
}
