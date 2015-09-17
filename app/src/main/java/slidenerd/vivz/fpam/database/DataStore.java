package slidenerd.vivz.fpam.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Keys;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.json.realm.RealmGroup;

/**
 * Created by vivz on 03/08/15.
 */
public class DataStore {
    private static Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaringClass().equals(RealmObject.class);
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    /**
     * In the first step, check if the list of groups to be stored is empty. If we have 1-N groups to store, use shared preferences to do the same. Convert the list of groups into a JSON string and store that.
     *
     * @param listGroups
     */
    public static void storeGroups(Realm realm, Context context, ArrayList<Group> listGroups) {
        ArrayList<RealmGroup> listRealmGroups = new ArrayList<>(listGroups.size());
        for (Group group : listGroups) {
            RealmGroup realmGroup = new RealmGroup();
            realmGroup.setId(group.getId());
            realmGroup.setName(group.getName());
            realmGroup.setIcon(group.getIcon());
            realmGroup.setUnread(group.getUnread());
            listRealmGroups.add(realmGroup);
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(listRealmGroups);
        realm.commitTransaction();
    }

    /**
     * Notice the default value for the json String that contains all groups. If the JSON String is null, our ArrayList will be null, if the json String is empty, our ArrayList will be null, however if our JSON String has a default value of [], our ArrayList will be empty and not null. Our objective is to ensure the ArrayList does not get a null value if something goes wrong.
     *
     * @return a list of groups that were retrieved from the backend, if the admin owns no groups or if there was a problem while retrieving data, then return an empty list.
     */
    public static ArrayList<Group> loadGroups(Realm realm, Context context) {

        RealmResults<RealmGroup> realmResults = realm.where(RealmGroup.class).findAllSorted("name");
        ArrayList<Group> listGroups = new ArrayList<>(20);
        for (RealmGroup group : realmResults) {
            //To avoid the below error, I simply duplicated the RealmResults so that it doesnt get passed to the background thread Caused by: java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
            Group fbGroup = new Group(group.getId(), group.getName(), group.getIcon(), group.getUnread());
            listGroups.add(fbGroup);
        }
        return listGroups;
    }

    /**
     * In the first step, check if we have a valid user to store. If we have a valid user, use shared preferences to store each aspect of their profile. Convert the 'Picture' object of the user into a JSON String and store that.
     *
     * @param admin the person using this app as an admin whose details you want to store in the backend.
     */
    public static void storeAdmin(Context context, Admin admin) {
        if (admin != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String adminJson = gson.toJson(admin);
            editor.putString(Keys.PREF_ADMIN, adminJson);
            editor.apply();
        } else {
            L.m("We could not retrieve your profile");
        }
    }

    /**
     * The fromJson method throws a JsonSyntaxException - if json is not a valid representation for an object of type typeOfT and an object of type T from the json. Returns null if json is null.
     *
     * @return an admin whose account is currently logged in if the login was successful, on any error, it returns null
     */
    @Nullable
    public static Admin loadAdmin(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String adminJson = sharedPreferences.getString(Keys.PREF_ADMIN, null);
        Admin admin = gson.fromJson(adminJson, Admin.class);
        return admin;
    }

}