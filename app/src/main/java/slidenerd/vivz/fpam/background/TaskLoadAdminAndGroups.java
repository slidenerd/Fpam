package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.google.gson.Gson;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;

@EFragment
public class TaskLoadAdminAndGroups extends Fragment {
    TaskCallback mCallback;

    public TaskLoadAdminAndGroups() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (TaskCallback) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (TaskCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Prior to loading groups, remember that we use a custom deserializer that takes the json and generates a new group object whose collection is returned as an ArrayList which we store in our realm database. If the user started this app before and loaded the feed for one or more groups, the 'timestamp' attribute of each group contains the last loaded timestamp which is then used to decide whether all the posts should be loaded or only a few. The user may select one or more groups for background monitoring and those choices need to be saved before the deserializer overwrites everything. Fetch the list of groups that were stored previously and extract their timestamp and monitored parameter in the first step. Use the deserializer to create new objects from the freshly retrieved JSON feed and overwrite the timestamp and monitored fields in the new group object if it was loaded previously.
     *
     * @param accessToken
     */
    @Background
    void loadAdminAndGroupsInBackground(AccessToken accessToken) {
        Realm realm = null;
        try {
            Gson gson = Fpam.getGson();
            realm = Realm.getDefaultInstance();
            Admin admin = FBUtils.requestMeSync(accessToken, gson);
            if (admin == null) {
                L.m("Fpam encountered problems downloading admin data, hence admin and groups data have not been downloaded");
                return;
            }

            //Get the list of groups retrieved in the previous round.

            RealmResults<Group> results = realm.where(Group.class).findAll();

            //Copy their mIds, timestamp and whether they are monitored in the background

            String[] ids = new String[results.size()];
            long[] timestamps = new long[results.size()];
            boolean[] monitored = new boolean[results.size()];
            for (int i = 0; i < results.size(); i++) {
                Group group = results.get(i);
                ids[i] = group.getId();
                timestamps[i] = group.getTimestamp();
                monitored[i] = group.isMonitored();
            }

            //Request the fresh list of group objects from the JSON feed. For each group object in this list, its monitored and timestamp are set at default.

            ArrayList<Group> groups = FBUtils.requestGroupsSync(accessToken, gson);

            //If a newly loaded group id matches with a previously loaded group id, then update the value of its timestamp and set its monitored parameter accordingly.

            for (Group group : groups) {
                for (int j = 0; j < results.size(); j++) {
                    if (group.getId().equals(ids[j])) {
                        group.setMonitored(monitored[j]);
                        group.setTimestamp(timestamps[j]);
                    }
                }
            }

            //Store the fully constructed group objects to the realm database.

            DataStore.storeAdmin(realm, admin);
            DataStore.storeGroups(realm, groups);

        } catch (JSONException e) {
            L.m("" + e);
        } finally {
            if (realm != null) {
                realm.close();
            }
            onAdminAndGroupsLoaded();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @UiThread
    void onAdminAndGroupsLoaded() {
        if (mCallback != null) {
            mCallback.afterAdminAndGroupsLoaded();
        }
    }

    public interface TaskCallback {
        void afterAdminAndGroupsLoaded();
    }
}