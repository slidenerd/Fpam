package slidenerd.vivz.fpam.ui;


import android.app.ProgressDialog;
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

import java.util.ArrayList;
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
import slidenerd.vivz.fpam.model.pojo.DeleteRequestInfo;
import slidenerd.vivz.fpam.model.pojo.DeleteResponseInfo;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.ModelUtils;
import slidenerd.vivz.fpam.util.NavUtils;
import slidenerd.vivz.fpam.widget.RecyclerViewEmptySupport;


/**
 * TODO handle error conditions, handle the loginmanager properly, how the items are shown while deleting and after deleting, move the background activities to a retained fragment, move the scroll position to wherever the user was previously after restoring adapter on delete
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
    private RealmResults<Post> mResults;

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
        if (!FBUtils.isValidAndCanPublish(mApplication.getToken())) {
            mLoginManager.logInWithPublishPermissions(FragmentPosts.this, Arrays.asList(Constants.PUBLISH_ACTIONS));
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
        mResults = mRealm.where(Post.class).beginsWith("postId", "NONE").findAll();
        mAdapter = new PostAdapter(getActivity(), mRealm, mResults);
        mAdapter.setHasStableIds(true);
        mAdapter.setDeleteListener(this);
        mTextEmpty = (TextView) view.findViewById(R.id.text_empty_posts);
        mRecyclerPosts = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_posts);
        mRecyclerPosts.setEmptyView(mTextEmpty);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPosts.setAdapter(mAdapter);
        mRecyclerPosts.setHasFixedSize(false);
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
        AccessToken accessToken = loginResult.getAccessToken();
        mApplication.setToken(accessToken);
    }

    /**
     * TODO handle the case where the user cancels while requesting publish_actions permission
     */
    @Override
    public void onCancel() {
        L.m("You cancelled");
    }

    /**
     * TODO handle any errors that may arise while login
     *
     * @param e
     */
    @Override
    public void onError(FacebookException e) {
        L.m("onError " + e);
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
    void onDelete(AccessToken token, int position, Group group, Post post) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Post> results = realm.where(Post.class).beginsWith("postId", group.getId()).findAllSorted("updatedTime", false);
            //realm.where(Post.class).equalTo("userId", post.getUserId()).findAll();
            ArrayList<DeleteRequestInfo> deletes = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                Post current = results.get(i);
                if (current.getUserId().equals(post.getUserId())) {
                    DeleteRequestInfo info = new DeleteRequestInfo(i, current);
                    deletes.add(info);
                }
            }
            if (!deletes.isEmpty()) {
                ArrayList<DeleteResponseInfo> infos = FBUtils.requestDeletePosts(token, deletes);

                String compositePrimaryKey = ModelUtils.getUserGroupCompositePrimaryKey(post.getUserId(), group.getId());

                realm.beginTransaction();
                int numberOfPostsDeleted = 0;
                for (DeleteResponseInfo info : infos) {

                    //If the post was removed successfully from the Facebook Graph API, remove the corresponding post from realm as well

                    if (info.getSuccess()) {

                        //Remove the post from Realm

                        realm.where(Post.class).equalTo("postId", info.getPost().getPostId()).findFirst().removeFromRealm();
                        numberOfPostsDeleted++;

                    } else {
                        L.m("Delete failed for " + info.getPost().getPostId() + " made by " + info.getPost().getUserName());
                    }
                }

                //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                realm.commitTransaction();

                //update the number of spam posts made by this spammer and the timestamp which indicates when this post was deleted

                if (numberOfPostsDeleted > 0) {
                    onItemsRemoved();
                    L.m("Delete successful by " + post.getUserName());
                    DataStore.storeOrUpdateSpammer(realm, compositePrimaryKey, post.getUserName(), numberOfPostsDeleted);
                }
            }
            afterDelete(position, true);
        } catch (JSONException e) {
            L.m(e + "");
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @UiThread
    void onItemsRemoved() {
        mResults = DataStore.loadPosts(mRealm, mSelectedGroup);
        mAdapter = new PostAdapter(getActivity(), mRealm, mResults);
        mRecyclerPosts.setAdapter(mAdapter);
    }


    @UiThread
    void afterDelete(int position, boolean success) {
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
        mResults = DataStore.loadPosts(mRealm, mSelectedGroup);
        mAdapter.setData(mResults);
        if (!mResults.isEmpty())
            mRecyclerPosts.smoothScrollToPosition(0);
    }
}
