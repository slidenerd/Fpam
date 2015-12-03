package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.google.gson.Gson;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.model.json.Admin;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.pojo.CollectionPayload;
import slidenerd.vivz.fpam.model.pojo.ObjectPayload;
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
        FacebookRequestError adminError = null, groupError = null;
        try {
            Gson gson = Fpam.getGson();
            realm = Realm.getDefaultInstance();
            ObjectPayload<Admin> admin = FBUtils.loadMe(accessToken, gson);

            //Request the fresh list of group objects from the JSON feed. For each group object in this list, its monitored and timestamp are set at default.

            CollectionPayload<Group> groups = FBUtils.loadGroups(accessToken, gson);

            //Store the fully constructed group objects to the realm database.

            if (admin.data != null) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(admin.data);
                realm.copyToRealmOrUpdate(groups.data);
                realm.commitTransaction();
            }
            adminError = admin.error;
            groupError = groups.error;

        } catch (JSONException e) {
            L.m("" + e);
        } finally {
            if (realm != null) {
                realm.close();
            }
            onAdminLoaded(adminError);
            onGroupsLoaded(groupError);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    @UiThread
    void onAdminLoaded(FacebookRequestError error) {
        if (mCallback != null) {
            mCallback.afterAdminLoaded(error);
        }
    }

    @UiThread
    void onGroupsLoaded(FacebookRequestError error) {
        if (mCallback != null) {
            mCallback.afterGroupsLoaded(error);
        }
    }

    public interface TaskCallback {
        void afterAdminLoaded(FacebookRequestError error);

        void afterGroupsLoaded(FacebookRequestError error);
    }
}