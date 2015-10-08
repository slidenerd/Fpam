package slidenerd.vivz.fpam.ui;

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
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;

@EFragment
public class TaskFragmentLogin extends Fragment {
    TaskCallback mCallback;

    public TaskFragmentLogin() {

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
            ArrayList<Group> listGroups = FBUtils.requestGroupsSync(accessToken, gson);
            DataStore.storeAdmin(realm, admin);
            DataStore.storeGroups(realm, listGroups);

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

    interface TaskCallback {
        void afterAdminAndGroupsLoaded();
    }
}