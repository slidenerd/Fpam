package slidenerd.vivz.fpam.background;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

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
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.ModelUtils;
import slidenerd.vivz.fpam.util.NavUtils;

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
    void deletePostsAsync(AccessToken token, int position, Post post) {
        Realm realm = null;
        Core core = new Core();
        try {
            realm = Realm.getDefaultInstance();
            Group group = realm.where(Group.class).equalTo("groupId", ModelUtils.computeGroupId(post.getPostId())).findFirst();
            boolean status = core.deletePostFB(position, token, group, post, realm);
            onPostsDeleted(status, position);

        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @UiThread
    void onPostsDeleted(boolean outcome, int position) {
        NavUtils.broadcastDeleteStatus(mContext, outcome, position);
    }
}