package slidenerd.vivz.fpam.database;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.admin.Picture;
import slidenerd.vivz.fpam.model.json.admin.PictureData;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.CopyUtils;

public class DataStore {

    /**
     * In the first step, check if the list of groups to be stored is empty. If we have 1-N groups to store, use shared preferences to do the same. Convert the list of groups into a JSON string and store that.
     */
    public static void storeGroups(Realm realm, JSONArray jsonArray) {
        realm.beginTransaction();
        realm.createOrUpdateAllFromJson(Group.class, jsonArray);
        realm.commitTransaction();
    }

    /**
     * Notice the default value for the json String that contains all groups. If the JSON String is null, our ArrayList will be null, if the json String is empty, our ArrayList will be null, however if our JSON String has a default value of [], our ArrayList will be empty and not null. Our objective is to ensure the ArrayList does not get a null value if something goes wrong.
     *
     * @return a list of groups that were retrieved from the backend, if the admin owns no groups or if there was a problem while retrieving data, then return an empty list.
     */
    public static ArrayList<Group> loadGroups(Realm realm) {

        RealmResults<Group> realmGroups = realm.where(Group.class).findAllSorted("name");
        return CopyUtils.duplicateGroups(realmGroups);
    }


    /**
     * In the first step, check if we have a valid user to store. If we have a valid user, use shared preferences to store each aspect of their profile. Convert the 'Picture' object of the user into a JSON String and store that.
     *
     * @param jsonObject the person using this app as an admin whose details you want to store in the backend.
     */
    public static void storeAdmin(Realm realm, JSONObject jsonObject) {
        //The Picture and PictureData classes don't have a primary key , so if we try to update Admin directly from JSON, a new entry is created for both of them each time, and hence we first remove all existing entries for each class first and then add a new entry
        realm.beginTransaction();
        realm.where(PictureData.class).findAll().clear();
        realm.where(Picture.class).findAll().clear();
        realm.where(Admin.class).findAll().clear();
        realm.createObjectFromJson(Admin.class, jsonObject);
        realm.commitTransaction();
    }

    /**
     * The fromJson method throws a JsonSyntaxException - if json is not a valid representation for an object of type typeOfT and an object of type T from the json. Returns null if json is null.
     *
     * @return an admin whose account is currently logged in if the login was successful, on any error, it returns null
     */
    @Nullable
    public static Admin loadAdmin(Realm realm) {
        //read the picture data first
        PictureData sourcePictureData = realm.where(PictureData.class).findFirst();
        Admin sourceAdmin = realm.where(Admin.class).findFirst();
        return CopyUtils.duplicateAdmin(sourcePictureData, sourceAdmin);
    }

    public static void storeFeed(Realm realm, JSONArray jsonArray) {
        realm.beginTransaction();
        realm.createOrUpdateAllFromJson(Post.class, jsonArray);
        realm.commitTransaction();
    }

    public static ArrayList<Post> loadFeed(Realm realm) {
        RealmResults<Post> realmPosts = realm.where(Post.class).findAll();
        return CopyUtils.duplicatePosts(realmPosts);
    }

}