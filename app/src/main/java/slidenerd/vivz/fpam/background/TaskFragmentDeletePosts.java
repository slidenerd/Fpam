package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.AccessToken;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import io.realm.Realm;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.core.Core;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.L;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_RESPONSE;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_OUTCOME;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POSITION;

/**
 * TODO handle the case where the delete fails or the person has deleted the post from Facebook directly instead of this app
 * The retained fragment used to load posts in the background
 */
@EFragment
public class TaskFragmentDeletePosts extends Fragment {
    @App
    Fpam mApplication;

    @Pref
    MyPrefs_ mPref;
    private Context mContext;

    public TaskFragmentDeletePosts() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * @param token the access token provided by facebook after login
     */
    @Background
    void deletePostsAsync(AccessToken token, int position, String postId) {
        Realm realm = null;
        Core core = new Core();
        try {
            realm = Realm.getDefaultInstance();
            boolean status = core.deletePostFB(token, postId, realm);
            onDelete(status, position);

        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @UiThread
    void onDelete(boolean outcome, int position) {
        Intent intent = new Intent(ACTION_DELETE_RESPONSE);
        intent.putExtra(EXTRA_OUTCOME, outcome);
        intent.putExtra(EXTRA_POSITION, position);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}