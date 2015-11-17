package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.extras.OccurrenceComparator;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Analytics;
import slidenerd.vivz.fpam.model.realm.Dailytics;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Occurrence;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_ID;
import static slidenerd.vivz.fpam.extras.Constants.POSTLYTICS_ID;
import static slidenerd.vivz.fpam.extras.Constants.POST_ID;
import static slidenerd.vivz.fpam.extras.Constants.SPAMMER_ID;
import static slidenerd.vivz.fpam.extras.Constants.TOP_ENTRIES_COUNT;

/**
 * TODO handle spammer authorization
 * Created by vivz on 10/11/15.
 */
public class Core {
    public List<Keyword> getKeywordsForGroup(Realm realm, String groupId) {
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

    public boolean deletePost(AccessToken token, String postId, Realm realm) throws JSONException {


        //use the post id to find the post object from the database
        Post post = realm.where(Post.class).equalTo(POST_ID, postId).findFirst();

        //get a list of keywords that apply to this group
        List<Keyword> keywords = getKeywordsForGroup(realm, post.getGroupId());

        //get the analytics object associated with this group
        Analytics analytics = realm.where(Analytics.class).equalTo(GROUP_ID, post.getGroupId()).findFirst();

        if (analytics == null) {
            analytics = new Analytics(post.getGroupId(), new RealmList<Occurrence>(), new RealmList<Spammer>());
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(analytics);
            realm.commitTransaction();
        }
        //Compute the unique id of a dailytics object with is the combination of group id and the current date in dd-MM-yyyy format
        String postlyticsId = ModelUtils.computePostlyticsId(post.getGroupId());

        //Get a reference to the current dailytics object for today
        Dailytics dailytics = realm.where(Dailytics.class).equalTo(POSTLYTICS_ID, postlyticsId).findFirst();

        //the Dailytics object cannot be null here since while scanning the posts it has already been created to store the number of scanned posts.

        if (dailytics == null) {
            return false;
        }

        //Get the top x items from the analytics for this group id
        RealmList<Occurrence> topX = analytics.getTopKeywords();

        boolean success = FBUtils.requestDeletePost(token, post);

        if (success) {

            boolean postWasEmpty = false;
            boolean postByExistingSpammer = false;
            boolean postContainsKeywords = false;
            //Compute the unique id for a spammer which is the combination of user id of a post and group id of the same post
            String spammerId = ModelUtils.computeSpammerId(post.getUserId(), post.getGroupId());

            //Check if this user exists in the spammer database for this particular group id
            Spammer spammer = realm.where(Spammer.class).equalTo(SPAMMER_ID, spammerId).findFirst();

            //if the spammer does not exist for the combination of this user id and group id, then create the spammer
            if (spammer == null) {
                spammer = new Spammer(spammerId, post.getUserName(), 1, System.currentTimeMillis(), false);
                realm.beginTransaction();
                realm.copyToRealm(spammer);
                realm.commitTransaction();
            }

            //update the existing spammer data in the database
            else {

                realm.beginTransaction();
                spammer.setUserName(post.getUserName());
                spammer.setSpamCount(spammer.getSpamCount() + 1);
                spammer.setLastActive(System.currentTimeMillis());
                realm.commitTransaction();
                postByExistingSpammer = true;
                //if the combination of user id and group id is not found in the database, create it
            }


            //Get the name, message, caption and description of the post and concatenate them as content for further processing purposes.
            String name = post.getName();
            String message = post.getMessage();
            String caption = post.getCaption();
            String description = post.getDescription();
            String content = StringUtils.join(message, name, caption, description, ' ');

            if (StringUtils.isBlank(message)) {

                //update the number of empty messages found under this group id as part of analytics
                postWasEmpty = true;
            }

            if (StringUtils.isNotBlank(content)) {

                OccurrenceComparator comparator = new OccurrenceComparator();
                ArrayList<Occurrence> occurrences = new ArrayList<>();

                //Convert the content to lowercase to find occurrence of each keyword in it.
                String lowercaseContent = content.toLowerCase();

                //Loop through the list of keywords
                for (Keyword keyword : keywords) {

                    //For each keyword, check the number of times it was found, our keyword is always in lowercase as per our app design which means we need to convert content to lowercase
                    int count = StringUtils.countMatches(lowercaseContent, keyword.getKeyword());

                    //if we have a non zero count, store the keyword and its occurrence and mark the boolean variable which indicates whether keywords were found in the content
                    if (count > 0) {
                        Occurrence occurrence = new Occurrence();
                        occurrence.setText(keyword.getKeyword());
                        occurrence.setCount(count);
                        occurrences.add(occurrence);
                        postContainsKeywords = true;
                    }
                }

                occurrences.addAll(0, topX);
                Collections.sort(occurrences, comparator);

                if (!occurrences.isEmpty()) {
                    for (int i = 0; i < TOP_ENTRIES_COUNT; i++) {
                        try {
                            topX.set(i, occurrences.get(i));
                        } catch (IndexOutOfBoundsException e) {
                            topX.add(occurrences.get(i));
                        }
                    }
                    realm.beginTransaction();
                    realm.copyToRealm(topX);
                    realm.commitTransaction();
                }
            }

            realm.beginTransaction();
            dailytics.setDeleted(dailytics.getDeleted() + 1);

            //If the post was made by a known spammer, increment the number of posts deleted so far because they were made by a known spammer
            if (postByExistingSpammer) {
                dailytics.setDeletedSpammer(dailytics.getDeletedSpammer() + 1);
            }

            //if the post had an empty message, increment the number of posts deleted so far because they had an empty message
            if (postWasEmpty) {
                dailytics.setDeletedEmpty(dailytics.getDeletedEmpty() + 1);
            }

            //if the post had keywords in it, increment the number of posts deleted so far because they had keywords in them
            if (postContainsKeywords) {
                dailytics.setDeletedKeywords(dailytics.getDeletedKeywords() + 1);
            }
            realm.commitTransaction();


            //update top x spammers as part of analytics

            //update top x keywords as part of analytics

        } else {
            //update delete failed count
            realm.beginTransaction();
            dailytics.setFailed(dailytics.getFailed() + 1);
            realm.commitTransaction();
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(dailytics);
//        analytics.setTopKeywords(topX);
        realm.copyToRealmOrUpdate(analytics);
        realm.commitTransaction();
        return success;
    }
}
