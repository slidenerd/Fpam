package slidenerd.vivz.fpam.ui;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.PostAdapter;
import slidenerd.vivz.fpam.adapter.SwipeHelper;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;
import slidenerd.vivz.fpam.widget.RecyclerViewEmptySupport;


/**
 * TODO handle error conditions, handle the loginmanager properly, how the items are shown while deleting and after deleting
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentPosts extends Fragment implements FacebookCallback<LoginResult>, PostAdapter.DeleteListener {

    private static final String STATE_SELECTED_GROUP = "group";
    @App
    Fpam mApplication;
    @InstanceState
    String mGroupId = Constants.GROUP_ID_NONE;
    private RecyclerViewEmptySupport mRecyclerPosts;
    private TextView mTextEmpty;
    private PostAdapter mAdapter;
    private Realm mRealm;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    private ProgressDialog mProgressDialog;
    private Group mSelectedGroup;
    private BroadcastReceiver mGroupSelectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public FragmentPosts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        //Initialize facebook stuff for login
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, this);

        //Check if our app has publish_actions permissions, its needed to perform deletes
        if (!mApplication.hasPermissionsPublishActions()) {
            mLoginManager.logInWithPublishPermissions(FragmentPosts.this, Arrays.asList("publish_actions"));
        }

        if (savedInstanceState != null) {
            mSelectedGroup = Parcels.unwrap(savedInstanceState.getParcelable(STATE_SELECTED_GROUP));
        }
        mProgressDialog = new ProgressDialog(getActivity());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SELECTED_GROUP, Parcels.wrap(Group.class, mSelectedGroup));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RealmResults<Post> results = mRealm.where(Post.class).beginsWith("postId", "NONE").findAll();
        mAdapter = new PostAdapter(getActivity(), mRealm, results);
        mAdapter.setDeleteListener(this);
        mTextEmpty = (TextView) view.findViewById(R.id.text_empty_posts);
        mRecyclerPosts = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_posts);
        mRecyclerPosts.setEmptyView(mTextEmpty);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPosts.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SwipeHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerPosts);
    }


    /**
     * TODO handle login success and check for permission publish_actions in the result, if its not found, show a prompt displaying why its needed
     *
     * @param loginResult
     */
    @Override
    public void onSuccess(LoginResult loginResult) {

    }

    /**
     * TODO handle the case where the user cancels while requesting publish_actions permission
     */
    @Override
    public void onCancel() {

    }

    /**
     * TODO handle any errors that may arise while login
     *
     * @param e
     */
    @Override
    public void onError(FacebookException e) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void triggerDelete(int position, Post post) {
        mProgressDialog.setTitle("Deleting post");
        mProgressDialog.setMessage("Post was made by " + post.getUserName());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        onDelete(mApplication.getToken(), position, mSelectedGroup, post);
    }

    @Background
    void onDelete(AccessToken accessToken, int position, Group group, Post post) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            boolean success = FBUtils.requestDeletePost(accessToken, post.getPostId());
            if (success) {
                //Since the post was removed from facebook graph api, remove it from realm as well
                realm.beginTransaction();
                realm.where(Post.class).equalTo("postId", post.getPostId()).findFirst().removeFromRealm();
                realm.commitTransaction();
                updateSpammerData(realm, group, post);
            }
            afterDelete(position, success);
        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void updateSpammerData(Realm realm, Group group, Post post) {

        //The spammer exists in the database if we find a composite id such that it starts with the user id the person who made the post and ends with the group id where the person posted

        Spammer spammer = realm.where(Spammer.class).beginsWith("userGroupCompositeId", post.getUserId()).endsWith("userGroupCompositeId", group.getId()).findFirst();

        //If we did NOT find a spammer for the given user id and group id, add the person to the spammer's database and mark the number of spam posts as 1 for this entry.

        if (spammer == null) {
            spammer = new Spammer(post.getUserId() + ":" + group.getId(), post.getUserName(), 1, System.currentTimeMillis());
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

    @UiThread
    void afterDelete(int position, boolean success) {
        if (success) {
            mAdapter.notifyItemRemoved(position);
        }
        mProgressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Receiver(actions = NavUtils.ACTION_LOAD_FEED, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onBroadcastSelectedGroup(Context context, Intent intent) {
        mSelectedGroup = Parcels.unwrap(intent.getExtras().getParcelable(NavUtils.EXTRA_SELECTED_GROUP));
//        FilterPostService_.intent(context).onFilterPosts(Parcels.wrap(Group.class, mSelectedGroup)).start();
        RealmResults<Post> results = DataStore.getSortedPostsFrom(mRealm, mSelectedGroup);
        mAdapter.setData(results);
        if (!results.isEmpty())
            mRecyclerPosts.smoothScrollToPosition(0);
    }

    @Receiver(actions = NavUtils.ACTION_FILTER_POSTS, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onBroadcastPostsFiltered() {
        L.m("posts filtered");

    }
}
