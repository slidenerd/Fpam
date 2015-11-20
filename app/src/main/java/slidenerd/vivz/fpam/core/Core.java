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

import static slidenerd.vivz.fpam.extras.Constants.COMPOSITE_GROUP_ORDER_ID;
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

                boolean isMessageEmpty = false;
                boolean byExistingSpammer = false;
                boolean hasSpamKeywords = false;
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
                    byExistingSpammer = true;
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
                    isMessageEmpty = true;
                }

                if (StringUtils.isNotBlank(content)) {

                    ArrayList<TopKeywords> top = new ArrayList<>(TOP_ENTRIES_COUNT);
                    for (int i = 0; i < TOP_ENTRIES_COUNT; i++) {

                        //Compute the primary key of each TopKeywords entry which is the combination of group id and the order such as 1,2,3...n
                        String compositeGroupOrderId = TopKeywords.computeGroupKeywordId(groupId, (i + 1));

                        //To store N top keywords for our group with id 'X' we use the primary keys X:1,X:2, X:3...X:n
                        TopKeywords current = realm.where(TopKeywords.class).equalTo(COMPOSITE_GROUP_ORDER_ID, compositeGroupOrderId).findFirst();

                        //we add the keyword to our list if its not null
                        if (current != null) {
                            top.add(current);
                        }
                    }
                    ArrayList<TopKeywords> list = new ArrayList<>();

                    //Convert the content to lowercase to find occurrence of each keyword in it.
                    String lowercaseContent = content.toLowerCase();
                    //Loop through the list of keywords
                    for (Keyword keyword : keywords) {

                        //For each keyword, check the number of times it was found, our keyword is always in lowercase as per our app design which means we need to convert content to lowercase
                        int count = StringUtils.countMatches(lowercaseContent, keyword.getKeyword());

                        //We assume that the same word will not be present in both the top list from the database and the content which we just scanned.
                        boolean duplicate = false;

                        //if we have a non zero count, store the keyword and its occurrence and mark the boolean variable which indicates whether keywords were found in the content
                        if (count > 0) {

                            //Loop through the list of top words to find if any of the words or phrases contained in the post are already present
                            for (int i = 0; i < top.size(); i++) {
                                TopKeywords current = top.get(i);

                                //if a word or phrase is present in both places
                                if (StringUtils.equals(current.getKeyword(), keyword.getKeyword())) {

                                    //update the number of times that particular word has been found which is the sum of the number of times it existed in the database and the number of times it was found in this post
                                    realm.beginTransaction();
                                    current.setCount(current.getCount() + count);
                                    realm.commitTransaction();

                                    //mark this word as duplicate
                                    duplicate = true;
                                    break;
                                }
                            }

                            //add the keyword to the list of keywords obtained by scanning the content as long as its not a duplicate
                            if (!duplicate) {
                                TopKeywords current = new TopKeywords();
                                current.setKeyword(keyword.getKeyword());
                                current.setCount(count);
                                list.add(current);
                            }

                            //since we have atleast one spam word or phrase, set this variable
                            hasSpamKeywords = true;
                        }
                    }

                    if (hasSpamKeywords) {
                        FrequencyComparator comparator = new FrequencyComparator();

                        //add the top keywords retrieved from the database to the list of words retrieved after scanning the contents of this post
                        list.addAll(top);

                        //sort all the posts in descending order of the number of times they were found + alphabetically descending whenver they are found the same number of times
                        Collections.sort(list, comparator);
                        realm.beginTransaction();

                        //we want to store only N entries or the number of entries obtained after adding the previous N entries in the database to the list of words obtained after scanning the current post whichever is smaller
                        for (int i = 0; i < list.size() && i < TOP_ENTRIES_COUNT; i++) {
                            TopKeywords current = list.get(i);

                            //if the current item already has a primary key, don't set a primary key since it causes a crash, otherwise set it
                            if (StringUtils.isBlank(current.getCompositeGroupOrderId())) {

                                //Construct and set the primary key.
                                String compositeGroupOrderId = TopKeywords.computeGroupKeywordId(groupId, (i + 1));
                                current.setCompositeGroupOrderId(compositeGroupOrderId);
                            }
                            realm.copyToRealmOrUpdate(current);
                        }
                        realm.commitTransaction();
                    }
                }

                realm.beginTransaction();
                dailytics.setDeleted(dailytics.getDeleted() + 1);

                //If the post was made by a known spammer, increment the number of posts deleted so far because they were made by a known spammer
                if (byExistingSpammer) {
                    dailytics.setDeletedSpammer(dailytics.getDeletedSpammer() + 1);
                }

                //if the post had an empty message, increment the number of posts deleted so far because they had an empty message
                if (isMessageEmpty) {
                    dailytics.setDeletedEmpty(dailytics.getDeletedEmpty() + 1);
                }

                //if the post had keywords in it, increment the number of posts deleted so far because they had keywords in them
                if (hasSpamKeywords) {
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
