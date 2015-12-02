package slidenerd.vivz.fpam.core;

import com.facebook.AccessToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Dailytics;
import slidenerd.vivz.fpam.model.pojo.Item;
import slidenerd.vivz.fpam.model.realm.TopKeyword;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.model.realm.TopSpammer;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.ALL;
import static slidenerd.vivz.fpam.extras.Constants.COUNT;
import static slidenerd.vivz.fpam.extras.Constants.GREATER;
import static slidenerd.vivz.fpam.extras.Constants.GROUPS;
import static slidenerd.vivz.fpam.extras.Constants.KEYWORD;
import static slidenerd.vivz.fpam.extras.Constants.LESS;
import static slidenerd.vivz.fpam.extras.Constants.POSTLYTICS_ID;
import static slidenerd.vivz.fpam.extras.Constants.SPAMMER_ID;
import static slidenerd.vivz.fpam.extras.Constants.TOP_ENTRIES_COUNT;

/**
 * TODO handle spammer authorization
 * Created by vivz on 10/11/15.
 */
public class Core {
    public List<Keyword> getKeywordsFor(Realm realm, String groupId) {

        //Return those keywords whose 'groups' attribute contains ALL indicating that this keyword applies to all groups or contains the current group id
        return realm.where(Keyword.class).equalTo(GROUPS, ALL).or().contains(GROUPS, groupId).findAllSorted(KEYWORD);
    }

    public boolean deletePost(AccessToken token, String postId) throws JSONException {
        return FBUtils.requestDelete(token, postId);
    }

    public boolean[] deletePosts(AccessToken token, ArrayList<Item> items) throws JSONException {
        ArrayList<String> postIds = new ArrayList<>(items.size());
        for (Item item : items) {
            postIds.add(item.postId);
        }
        return FBUtils.requestDeletes(token, postIds, items.size());
    }

    public void filterPosts(AccessToken token, Realm realm, String groupId, ArrayList<Post> posts) throws JSONException {

        RealmResults<Spammer> spammers = realm.where(Spammer.class).beginsWith(SPAMMER_ID, groupId).findAllSorted(COUNT);
        List<Keyword> keywords = getKeywordsFor(realm, groupId);

        int scanned = posts.size();
        int deleted = 0;
        int deletedEmpty = 0;
        int deletedKeywords = 0;
        int deletedSpammer = 0;
        int failed = 0;
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {

            Post post = posts.get(i);

            //Check if we have a valid post before processing
            if (Post.isValidPost(post)) {
                boolean bySpammer = false;
                boolean byKeyword = false;

                //Store the spammer in this variable if this post was made by a spammer.
                Spammer spammer = null;
                for (Spammer current : spammers) {

                    //Check if the id of the person making this post is already present in the list of spammers retrieved from our database
                    if (StringUtils.contains(current.getCompositeGroupUserId(), post.getUserId())) {
                        bySpammer = true;
                        spammer = current;
                        break;
                    }
                }


                String content = Post.getContent(post);

                //Browse through the list of stored keywords relevant to our group id and check if our content contains any of these keywords
                for (Keyword keyword : keywords) {

                    //Check if the content of the post contains any keyword from our list of relevant keywords for this group id
                    if (StringUtils.contains(content, keyword.getKeyword())) {
                        byKeyword = true;
                        break;
                    }
                }
                //If the post was made by a spammer or contains a keyword, add this post to the list of posts to be deleted.
                if (bySpammer || byKeyword) {

                    //This item indicates a post to be deleted with certain characteristics
                    Item item = new Item();
                    item.position = i;
                    item.postId = post.getPostId();
                    item.userId = post.getUserId();
                    item.userName = post.getUserName();
                    item.empty = StringUtils.isBlank(post.getMessage());
                    item.content = Post.getContent(post);
                    item.bySpammer = bySpammer;
                    item.byKeyword = byKeyword;
                    item.spammer = spammer;
                    //By default the status of each post's delete is failed
                    item.status = false;
                    //Add this item to the list of items we want to delete.
                    items.add(item);
                }
            }
        }

        if (!items.isEmpty()) {

            //Request all the posts to be deleted, this method modifies the ArrayList to update the status variable as true or false depending on the outcome
            boolean[] statuses = deletePosts(token, items);

            //This String will contain the combination of 'name', 'message', 'caption' and 'description' from all the posts to compute the occurrence of each keyword at the end to update top keywords list
            StringBuilder totalContent = new StringBuilder("");
            for (int i = items.size() - 1; i >= 0; i--) {

                Item item = items.get(i);
                item.status = statuses[i];
                //Post was successfully deleted
                if (item.status) {

                    //Increment the number of posts deleted so far.
                    deleted++;

                    //Append the 'content' of the current post to the contents of all the posts so far.
                    totalContent.append(StringUtils.join(item.content, ' '));

                    //This post has a blank 'messsage' which means its an empty post.
                    if (item.empty) {
                        deletedEmpty++;
                    }

                    //This post was made by a spammer
                    if (item.bySpammer) {
                        deletedSpammer++;
                    }

                    //This post contains a spam word or phrase or keyword
                    if (item.byKeyword) {
                        deletedKeywords++;
                    }

                    createOrUpdateSpammer(realm, groupId, item.userId, item.userName, item.spammer);

                    posts.remove(item.position);
                }

                //Post delete failed
                else {
                    failed++;
                }
            }
            updateTopKeywords(realm, groupId, totalContent.toString(), keywords);
            updateTopSpammers(realm, groupId);
        }
        createOrUpdateDailytics(realm, groupId, scanned, deleted, deletedEmpty, deletedKeywords, deletedSpammer, failed);
    }

    public Map<String, Integer> computeOccurrences(String content, List<Keyword> keywords, Map<String, Integer> occurrences) {
        //Browse through the list of stored keywords relevant to our group id and check if our content contains any of these keywords
        for (Keyword keyword : keywords) {

            //Check the number of times the current keyword was found in our content
            int count = StringUtils.countMatches(content, keyword.getKeyword());

            //If the current keyword appears atleast once in our content
            if (count > 0) {
                String word = keyword.getKeyword();

                //Check if the current keyword was already added to the occurrences list
                if (occurrences.containsKey(word)) {

                    // Map already contains the word key. Just increment it's count by 1
                    occurrences.put(word, occurrences.get(word) + count);
                } else {

                    // Map doesn't have mapping for word. Add one with count = 1
                    occurrences.put(word, count);
                }
            }
        }
        return occurrences;
    }

    public void createOrUpdateSpammer(Realm realm, String groupId, String userId, String userName, Spammer spammer) {

        realm.beginTransaction();
        if (spammer == null) {

            //Compute the unique id for a spammer which is the combination of user id of a post and group id of the same post
            String spammerId = Spammer.computeId(groupId, userId);

            //Create the spammer with the specified id, name, number of spam posts as 1 and last active time as the current time
            spammer = new Spammer(spammerId, userName, 1, System.currentTimeMillis(), false);
            realm.copyToRealmOrUpdate(spammer);
        } else {
            spammer.setUserName(userName);
            spammer.setCount(spammer.getCount() + 1);
            spammer.setLastActive(System.currentTimeMillis());
        }
        realm.commitTransaction();
    }

    public boolean createOrUpdateSpammer(Realm realm, String groupId, String userId, String userName) {

        //Compute the unique id for a spammer which is the combination of user id of a post and group id of the same post
        String spammerId = Spammer.computeId(groupId, userId);

        //Check if this user exists in the spammer database for this particular group id
        Spammer spammer = realm.where(Spammer.class).equalTo(SPAMMER_ID, spammerId).findFirst();

        //if the spammer does not exist for the combination of this user id and group id, then create the spammer

        boolean byExistingSpammer = false;
        realm.beginTransaction();
        if (spammer == null) {

            //Create the spammer with the specified id, name, number of spam posts as 1 and last active time as the current time
            spammer = new Spammer(spammerId, userName, 1, System.currentTimeMillis(), false);
            realm.copyToRealmOrUpdate(spammer);
        }

        //Update the existing spammer data in the database
        else {
            spammer.setUserName(userName);

            //Increase the number of spam posts made by the spammer so far by 1
            spammer.setCount(spammer.getCount() + 1);
            spammer.setLastActive(System.currentTimeMillis());
            byExistingSpammer = true;
            //if the combination of user id and group id is not found in the database, create it
        }
        realm.commitTransaction();
        return byExistingSpammer;
    }

    public void updateTopKeywords(Realm realm, String groupId, String content, List<Keyword> keywords) {

        //Store the number of times each word was found along with its count
        Map<String, Integer> occurrences = new HashMap<>(TOP_ENTRIES_COUNT);

        //Get the list of existing top keywords stored in our database sorted in a descending order by the number of times first and alphabetically second
        RealmResults<TopKeyword> topKeywords = realm.where(TopKeyword.class).findAllSorted(COUNT, true, KEYWORD, true);
        for (TopKeyword keyword : topKeywords) {

            String word = keyword.getKeyword();
            int count = keyword.getCount();

            //add each word and its count to our occurrences list
            occurrences.put(word, count);
        }

        computeOccurrences(content, keywords, occurrences);

        //Sort the occurrences list in the descending order by the number of times each word is found and for words with the same count, sort it in the descending order alphabetically
        List<Map.Entry<String, Integer>> l = sortDescending(occurrences);

        //Our list that will contain the new top keywords
        List<TopKeyword> top = new ArrayList<>();

        //Loop as long as either we are within the bounds of the list or within the number of top entries we need to retrieve whichever is smaller.
        for (int i = 0; i < l.size() && i < TOP_ENTRIES_COUNT; i++) {

            Map.Entry<String, Integer> entry = l.get(i);

            //Compute the top keyword id which is the combination of our group id in which this keyword was found and index such as 1,2,3...n which indicates the rank of the top keyword
            String id = TopKeyword.computeGroupOrderId(groupId, i + 1);

            //Add a new top keyword with the specified id, the word itself and the number of times it was found.
            top.add(new TopKeyword(id, entry.getKey(), entry.getValue()));
        }

        //Update the existing list of top keywords with the new ones.
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(top);
        realm.commitTransaction();
    }

    public void updateTopSpammers(Realm realm, String groupId) {

        //This list will contain our top spammers.
        List<TopSpammer> topSpammers = new ArrayList<>(TOP_ENTRIES_COUNT);

        //Find all spammers for this group id sorted descending by the number of times they have spammed in this group
        RealmResults<Spammer> spammers = realm.where(Spammer.class).beginsWith(SPAMMER_ID, groupId).findAllSorted(COUNT, false);

        //Loop as long as we are within the bounds of the list of top spammers or the number of top entries we need to retrieve whichever is smaller.
        for (int i = 0; i < spammers.size() && i < TOP_ENTRIES_COUNT; i++) {

            Spammer spammer = spammers.get(i);

            //Compute the top spammer id which is a combination of the group id under which they spam followed by an index such as 1,2,3...n which indicates their top position rank
            String topSpammerId = TopSpammer.computeGroupOrderId(groupId, (i + 1));

            //Compute the user id of this spammer from the combination of group id and user id
            String userId = Spammer.computeUserId(spammer.getCompositeGroupUserId());

            //Add a new top spammer with the specified id, user id, name, and the number of times the person has spammed.
            topSpammers.add(new TopSpammer(topSpammerId, userId, spammer.getUserName(), spammer.getCount()));
        }

        //Update the existing list of top spammers with the new data.
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(topSpammers);
        realm.commitTransaction();
    }

    public void createOrUpdateDailytics(Realm realm, String groupId, int scanned, int deleted, int deletedEmpty, int deletedKeywords, int deletedSpammer, int failed) {
        //Compute the unique id of a dailytics object with is the combination of group id and the current date in dd-MM-yyyy format
        String dailyticsId = Dailytics.computeId(groupId);

        //Get a reference to the current dailytics object for today
        Dailytics dailytics = realm.where(Dailytics.class).equalTo(POSTLYTICS_ID, dailyticsId).findFirst();
        realm.beginTransaction();
        if (dailytics == null) {
            dailytics = new Dailytics(dailyticsId, scanned, deleted, deletedEmpty, deletedKeywords, deletedSpammer, failed);
            realm.copyToRealmOrUpdate(dailytics);
        } else {
            dailytics.setScanned(dailytics.getScanned() + scanned);
            dailytics.setDeleted(dailytics.getDeleted() + deleted);
            dailytics.setDeletedEmpty(dailytics.getDeletedEmpty() + deletedEmpty);
            dailytics.setDeletedKeywords(dailytics.getDeletedKeywords() + deletedKeywords);
            dailytics.setDeletedSpammer(dailytics.getDeletedSpammer() + deletedSpammer);
            dailytics.setFailed(dailytics.getFailed() + failed);
        }
        realm.commitTransaction();
    }

    public void updateDailytics(Realm realm, String groupId, boolean status, boolean empty, boolean spammer) {
        //Compute the unique id of a dailytics object with is the combination of group id and the current date in dd-MM-yyyy format
        String dailyticsId = Dailytics.computeId(groupId);

        //Get a reference to the current dailytics object for today
        Dailytics dailytics = realm.where(Dailytics.class).equalTo(POSTLYTICS_ID, dailyticsId).findFirst();

        //A dailytics object is created at the time of loading posts to track the number of posts loaded. This step occurs after the posts have been loaded which means we need to have a valid dailytics object by now. If we don' have a valid object at this point, skip processing and return.
        if (dailytics == null) {
            return;
        }

        realm.beginTransaction();

        //If we successfully deleted a post from Facebook, update
        if (status) {

            //Number of deleted posts so far.
            dailytics.setDeleted(dailytics.getDeleted() + 1);

            //Number of posts deleted so far whose 'message' was empty indicating it was a blank post.
            dailytics.setDeletedEmpty(dailytics.getDeletedEmpty() + (empty ? 1 : 0));

            //Number of posts deleted so far made by a previously marked spammer indicating existing spammers activity.
            dailytics.setDeletedSpammer(dailytics.getDeletedSpammer() + (spammer ? 1 : 0));
        } else {

            //If the delete failed, increment the number of failures so far.
            dailytics.setFailed(dailytics.getFailed() + 1);
        }
        realm.commitTransaction();

    }

    static List<Map.Entry<String, Integer>> sortDescending(Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                int lhs = e1.getValue();
                int rhs = e2.getValue();

                //Sort by descending order of count for two entries.
                if (rhs > lhs) {
                    return GREATER;
                } else if (rhs < lhs) {
                    return LESS;
                } else {

                    //Sort by descending alphabetical order if two entries have the same count
                    return e2.getKey().compareTo(e1.getKey());
                }
            }
        });

        return sortedEntries;
    }
}
