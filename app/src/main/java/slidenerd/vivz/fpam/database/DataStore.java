package slidenerd.vivz.fpam.database;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.CopyUtils;

public class DataStore {

    /**
     * In the first step, check if the list of groups to be stored is empty. If we have 1-N groups to store, use shared preferences to do the same. Convert the list of groups into a JSON string and store that.
     */
    public static void storeGroups(Realm realm, ArrayList<Group> groups) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(groups);
        realm.commitTransaction();
    }

    /**
     * Notice the default value for the json String that has all groups. If the JSON String is null, our ArrayList will be null, if the json String is empty, our ArrayList will be null, however if our JSON String has a default value of [], our ArrayList will be empty and not null. Our objective is to ensure the ArrayList does not get a null value if something goes wrong.
     *
     * @return a list of groups that were retrieved from the backend, if the admin owns no groups or if there was a problem while retrieving data, then return an empty list.
     */
    public static ArrayList<Group> copyLoadGroups(Realm realm) {
        RealmResults<Group> realmGroups = realm.where(Group.class).findAllSorted("name");
        return CopyUtils.duplicateGroups(realmGroups);
    }

    public static RealmResults<Group> loadGroups(Realm realm) {
        return realm.where(Group.class).findAllSorted("name");
    }

    public static long getLastLoadedTimestamp(Realm realm, String groupId) {
        Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();
        return group != null ? group.getTimestamp() : 0;
    }

    /**
     * In the first step, check if we have a valid user to store. If we have a valid user, use shared preferences to store each aspect of their profile. Convert the 'Picture' object of the user into a JSON String and store that.
     *
     * @param admin the person using this app as an admin whose details you want to store in the backend.
     */
    public static void storeAdmin(Realm realm, Admin admin) {
        //The Picture and PictureData classes don't have a primary key , so if we try to update AdminFields directly from JSON, a new entry is created for both of them each time, and hence we first remove all existing entries for each class first and then add a new entry
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(admin);
        realm.commitTransaction();
    }

    /**
     * The fromJson method throws a JsonSyntaxException - if json is not a valid representation for an object of type typeOfT and an object of type T from the json. Returns null if json is null.
     *
     * @return an admin whose account is currently logged in if the login was successful, on any error, it returns null
     */
    @Nullable
    public static Admin copyLoadAdmin(Realm realm) {
        //read the picture data first
        Admin src = realm.where(Admin.class).findFirst();
        return src != null ? CopyUtils.duplicateAdmin(src) : null;
    }


    public static void limitStoredPosts(Realm realm, Group group, int maximumPostsStored) {
        RealmResults<Post> results = realm.where(Post.class).beginsWith("postId", group.getId()).findAllSorted("updatedTime", false);
        int numberOfPostsRemoved = 0;
        realm.beginTransaction();
        while (results.size() > maximumPostsStored) {
            Post post = results.get(results.size() - 1);
            post.removeFromRealm();
            numberOfPostsRemoved++;
        }
        realm.commitTransaction();
        L.m(numberOfPostsRemoved > 0 ? "Removed " + numberOfPostsRemoved : "Nothing to remove");
    }

    public static void storeOrUpdateSpammer(Realm realm, String compositePrimaryKey, String spammerName, int initialSpamCount) {

        //The spammer exists in the database if we find a composite id such that it starts with the user id the person who made the post and ends with the group id where the person posted

        Spammer spammer = realm.where(Spammer.class).equalTo("userGroupCompositeId", compositePrimaryKey).findFirst();

        //If we did NOT find a spammer for the given user id and group id, add the person to the spammer's database and mark the number of spam posts as 1 for this entry.

        if (spammer == null) {
            spammer = new Spammer(compositePrimaryKey, spammerName, initialSpamCount, System.currentTimeMillis(), false);
            realm.beginTransaction();
            realm.copyToRealm(spammer);
            realm.commitTransaction();
        }

        //If we found the id of the person making this post in the spammer's database, increment the number of spam posts made by this person.

        else {
            realm.beginTransaction();
            spammer.setSpamCount(spammer.getSpamCount() + 1);
            spammer.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
        }
    }
}