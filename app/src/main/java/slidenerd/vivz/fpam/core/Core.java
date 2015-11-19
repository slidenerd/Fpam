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
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.extras.FrequencyComparator;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Dailytics;
import slidenerd.vivz.fpam.model.realm.TopKeywords;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.COMPOSITE_GROUP_KEYWORD_ID;
import static slidenerd.vivz.fpam.extras.Constants.COUNT;
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

        boolean success = false;
        //use the post id to find the post object from the database
        Post post = realm.where(Post.class).equalTo(POST_ID, postId).findFirst();

        String groupId = post.getGroupId();

        if (StringUtils.isNotBlank(groupId)) {
            //get a list of keywords that apply to this group
            List<Keyword> keywords = getKeywordsForGroup(realm, groupId);

            //Compute the unique id of a dailytics object with is the combination of group id and the current date in dd-MM-yyyy format
            String dailyticsId = Dailytics.computeId(groupId);

            //Get a reference to the current dailytics object for today
            Dailytics dailytics = realm.where(Dailytics.class).equalTo(POSTLYTICS_ID, dailyticsId).findFirst();

            //the Dailytics object cannot be null here since while scanning the posts it has already been created to store the number of scanned posts.

            if (dailytics == null) {
                return false;
            }

            success = FBUtils.requestDeletePost(token, post);

            if (success) {

                boolean postWasEmpty = false;
                boolean postByExistingSpammer = false;
                boolean postContainsKeywords = false;
                //Compute the unique id for a spammer which is the combination of user id of a post and group id of the same post
                String spammerId = Spammer.computeId(post.getUserId(), groupId);

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

                    RealmResults<TopKeywords> topXKeywords = realm.where(TopKeywords.class).beginsWith(COMPOSITE_GROUP_KEYWORD_ID, groupId).findAllSorted(COUNT);
                    realm.beginTransaction();
                    for (int i = topXKeywords.size() - 1; i >= TOP_ENTRIES_COUNT; i--) {
                        topXKeywords.get(i).removeFromRealm();
                    }
                    realm.commitTransaction();
                    FrequencyComparator comparator = new FrequencyComparator();
                    ArrayList<TopKeywords> frequencies = new ArrayList<>();

                    //Convert the content to lowercase to find occurrence of each keyword in it.
                    String lowercaseContent = content.toLowerCase();

                    //Loop through the list of keywords
                    for (Keyword keyword : keywords) {

                        //For each keyword, check the number of times it was found, our keyword is always in lowercase as per our app design which means we need to convert content to lowercase
                        int count = StringUtils.countMatches(lowercaseContent, keyword.getKeyword());

                        //if we have a non zero count, store the keyword and its occurrence and mark the boolean variable which indicates whether keywords were found in the content
                        if (count > 0) {
                            TopKeywords topKeywords = new TopKeywords();
                            String frequencyId = TopKeywords.computeGroupKeywordId(groupId, keyword.getKeyword());
                            topKeywords.setCompositeGroupKeywordId(frequencyId);
                            topKeywords.setCount(count);
                            frequencies.add(topKeywords);
                            postContainsKeywords = true;
                        }
                    }
                    frequencies.addAll(topXKeywords);
                    if (postContainsKeywords) {
                        Collections.sort(frequencies, comparator);
                        realm.beginTransaction();
                        for (int i = 0; i < frequencies.size() && i < TOP_ENTRIES_COUNT; i++) {
                            for (TopKeywords topKeywords : topXKeywords) {
                                String word1 = TopKeywords.getKeyword(topKeywords.getCompositeGroupKeywordId());
                                String word2 = TopKeywords.getKeyword(frequencies.get(i).getCompositeGroupKeywordId());
                                if (StringUtils.equals(word1, word2)) {
                                    topKeywords.setCount(topKeywords.getCount() + 1);

                                }
                                realm.copyToRealmOrUpdate(topKeywords);
                                break;
                            }
                        }
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
//            analytics.setTopKeywords(topX);
            realm.commitTransaction();
        } else {
            L.m("Did not find a valid group Id while deleting a post");
        }
        return success;
    }
}
