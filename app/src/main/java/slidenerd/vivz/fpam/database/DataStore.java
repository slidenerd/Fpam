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
import slidenerd.vivz.fpam.model.json.admin.FBAdmin;
import slidenerd.vivz.fpam.model.json.group.FBGroup;

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
    public static void storeGroups(Realm realm, Context context, ArrayList<FBGroup> listGroups) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(listGroups);
        realm.commitTransaction();
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String data = sGson.toJson(listGroups);
//        editor.putString(Keys.PREF_GROUPS, data);
//        editor.apply();
    }

    /**
     * Notice the default value for the json String that contains all groups. If the JSON String is null, our ArrayList will be null, if the json String is empty, our ArrayList will be null, however if our JSON String has a default value of [], our ArrayList will be empty and not null. Our objective is to ensure the ArrayList does not get a null value if something goes wrong.
     *
     * @return a list of groups that were retrieved from the backend, if the admin owns no groups or if there was a problem while retrieving data, then return an empty list.
     */
    public static ArrayList<FBGroup> loadGroups(Realm realm, Context context) {

        RealmResults<FBGroup> realmResults = realm.where(FBGroup.class).findAllSorted("name");
        ArrayList<FBGroup> listGroups = new ArrayList<>(realmResults.size());
        for (FBGroup group : realmResults) {
            /*
            To avoid the below error, I simply duplicated the RealmResults so that it doesnt get passed to the background thread
            Caused by: java.lang.IllegalStateException: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
            at io.realm.Realm.checkIfValid(Realm.java:192)
            at io.realm.FBGroupRealmProxy.getId(FBGroupRealmProxy.java:51)
            at slidenerd.vivz.fpam.util.FBUtils.requestFeedSync(FBUtils.java:111)
            at slidenerd.vivz.fpam.background.TaskLoadFeed.doInBackground(TaskLoadFeed.java:35)
            at slidenerd.vivz.fpam.background.TaskLoadFeed.doInBackground(TaskLoadFeed.java:19)
             */
            FBGroup fbGroup = new FBGroup(group.getId(), group.getName(), group.getIsAdministrator(), group.getIconUrl(), group.getUnreadCount());
            listGroups.add(fbGroup);
        }
        return listGroups;
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String jsonGroups = sharedPreferences.getString(Keys.PREF_GROUPS, "[]");
//        ArrayList<FBGroup> listGroups = sGson.fromJson(jsonGroups, new TypeToken<ArrayList<FBGroup>>() {
//        }.getType());
//        return listGroups;
    }

    /**
     * In the first step, check if we have a valid user to store. If we have a valid user, use shared preferences to store each aspect of their profile. Convert the 'Picture' object of the user into a JSON String and store that.
     *
     * @param admin the person using this app as an admin whose details you want to store in the backend.
     */
    public static void storeAdmin(Context context, FBAdmin admin) {
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
    public static FBAdmin loadAdmin(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String adminJson = sharedPreferences.getString(Keys.PREF_ADMIN, null);
        FBAdmin admin = gson.fromJson(adminJson, FBAdmin.class);
        return admin;
    }

}
