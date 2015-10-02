package slidenerd.vivz.fpam.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.ApplicationFpam;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;

@EFragment
public class FeedTaskFragment extends Fragment {
    TaskCallback mCallback;

    public FeedTaskFragment() {

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
    void loadFeed(@NonNull Group group, AccessToken accessToken) {
        if (FBUtils.isValidToken(accessToken)) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                ArrayList<Post> listPosts = FBUtils.requestFeedSync(accessToken, ApplicationFpam.getGson(), group);
                DataStore.storeFeed(realm, listPosts);
                onFeedLoaded("FeedFields Loaded For", group);
            } catch (JSONException e) {
                L.m("" + e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        } else {
            onFeedLoaded("Did not find a valid access token while loading", group);
        }
    }

    @UiThread
    void onFeedLoaded(String message, Group group) {
        if (mCallback != null) {
            mCallback.onFeedLoaded(message, group);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    interface TaskCallback {
        void onFeedLoaded(String message, Group group);
    }
}