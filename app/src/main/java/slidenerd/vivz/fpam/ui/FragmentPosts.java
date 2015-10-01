package slidenerd.vivz.fpam.ui;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.ApplicationFpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.PostAdapter;
import slidenerd.vivz.fpam.adapter.TouchHelper;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.widget.RecyclerViewEmptySupport;

/**
 * TODO handle error conditions, handle the loginmanager properly, how the items are shown while deleting and after deleting
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentPosts extends Fragment implements FacebookCallback<LoginResult>, PostAdapter.OnDeleteListener {

    @InstanceState
    String mGroupId = Constants.GROUP_ID_NONE;
    private RecyclerViewEmptySupport mRecyclerPosts;
    private TextView mTextEmpty;
    private PostAdapter mAdapter;
    private Realm mRealm;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mGroupSelectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Group selectedGroup = Parcels.unwrap(intent.getExtras().getParcelable("selectedGroup"));
            RealmResults<Post> realmResults = mRealm.where(Post.class).beginsWith("postId", selectedGroup.getId()).findAllSorted("updatedTime", false);
            mAdapter.setData(realmResults);
        }
    };

    public FragmentPosts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, this);
        AccessToken accessToken = ApplicationFpam.getFacebookAccessToken();
        Set<String> permissions = accessToken.getPermissions();
        if (!permissions.contains("publish_actions")) {
            mLoginManager.logInWithPublishPermissions(FragmentPosts.this, Arrays.asList("publish_actions"));
        }
        mProgressDialog = new ProgressDialog(getActivity());
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
        mAdapter.setOnDeleteListener(this);
        mTextEmpty = (TextView) view.findViewById(R.id.text_empty_posts);
        mRecyclerPosts = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_posts);
        mRecyclerPosts.setEmptyView(mTextEmpty);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPosts.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new TouchHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerPosts);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGroupSelectedReceiver, new IntentFilter("group_selected"));
    }


    @Override
    public void onSuccess(LoginResult loginResult) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException e) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void beforeDelete(int position, Post post) {
        mProgressDialog.setTitle("Deleting post");
        mProgressDialog.setMessage("Post was made by " + post.getUserName());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        onDelete(ApplicationFpam.getFacebookAccessToken(), position, post.getPostId());
    }

    @Background
    void onDelete(AccessToken accessToken, int position, String postId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            boolean outcome = FBUtils.requestDeletePost(accessToken, postId);
            if (outcome) {
                realm.beginTransaction();
                realm.where(Post.class).equalTo("postId", postId).findFirst().removeFromRealm();
                realm.commitTransaction();
            }
            afterDelete(position, outcome);
        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGroupSelectedReceiver);
    }
}
