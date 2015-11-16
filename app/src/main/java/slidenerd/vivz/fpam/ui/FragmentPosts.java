package slidenerd.vivz.fpam.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.Receiver;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.AdapterPost;
import slidenerd.vivz.fpam.adapter.OnItemClickListener;
import slidenerd.vivz.fpam.adapter.RecyclerViewHelperImpl;
import slidenerd.vivz.fpam.adapter.SwipeToDismissTouchListener;
import slidenerd.vivz.fpam.adapter.SwipeableItemClickListener;
import slidenerd.vivz.fpam.core.Core;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_POST;
import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_RESPONSE;
import static slidenerd.vivz.fpam.extras.Constants.ACTION_LOAD_FEED;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_ID;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_OUTCOME;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POSITION;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_SELECTED_GROUP;

/**
 * TODO handle error conditions, handle the loginmanager properly, how the items are shown while deleting and after deleting, move the scroll position to wherever the user was previously after restoring adapter on delete
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentPosts extends Fragment implements FacebookCallback<LoginResult> {

    private static final String STATE_SELECTED_GROUP_ID = "group_id";
    @App
    Fpam mApplication;
    @InstanceState
    String mGroupId = Constants.GROUP_ID_NONE;
    private RecyclerView mRecycler;
    private AdapterPost mAdapter;
    private Realm mRealm;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    private String mSelectedGroupId;
    private RealmResults<Post> mPosts;
    private Context mContext;

    public FragmentPosts() {
        // Required empty public constructor
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
            mSelectedGroupId = savedInstanceState.getString(STATE_SELECTED_GROUP_ID);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SELECTED_GROUP_ID, mSelectedGroupId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosts = mRealm.where(Post.class).equalTo("postId", "NONE").findAll();
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler_posts);
        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AdapterPost(mContext, mRealm, mPosts);
        mAdapter.setHasStableIds(true);
        mRecycler.setAdapter(mAdapter);
        final SwipeToDismissTouchListener<RecyclerViewHelperImpl> mTouchListener = new SwipeToDismissTouchListener<>(
                new RecyclerViewHelperImpl(mRecycler),
                new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewHelperImpl>() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(RecyclerViewHelperImpl view, int position) {

                        //On swiping a post, broadcast its position and postId for further processing
                        Intent intent = new Intent(ACTION_DELETE_POST);
                        intent.putExtra(EXTRA_POSITION, position);
                        intent.putExtra(EXTRA_ID, mAdapter.getItem(position).getPostId());
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                });
        mRecycler.setOnTouchListener(mTouchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecycler.addOnScrollListener((RecyclerView.OnScrollListener) mTouchListener.makeScrollListener());
        mRecycler.addOnItemTouchListener(new SwipeableItemClickListener(mContext, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.txt_delete) {
                    mTouchListener.processPendingDismisses();
                } else if (view.getId() == R.id.txt_undo) {
                    mTouchListener.undoPendingDismiss();
                } else { // R.id.txt_data
                    //What to do when you click on a post?
                }
            }
        }));
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
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Receiver(actions = ACTION_LOAD_FEED, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onBroadcastSelectedGroup(Context context, Intent intent) {
        mSelectedGroupId = intent.getExtras().getString(EXTRA_SELECTED_GROUP);
        Core core = new Core();
        String deletes = Keyword.toString(core.getRelevantKeywords(mRealm, mSelectedGroupId));
        L.m(deletes);
        mPosts = mRealm.where(Post.class).beginsWith("postId", mSelectedGroupId).findAllSortedAsync("updatedTime", false);
        mPosts.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateRealmResults(mPosts);
            }
        });
    }

    @Receiver(actions = ACTION_DELETE_RESPONSE, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onDeleteResponse(Context context, Intent intent) {
        boolean outcome = intent.getExtras().getBoolean(EXTRA_OUTCOME);
        int position = intent.getExtras().getInt(EXTRA_POSITION);
        if (outcome) {
            L.m("notifying delete");
            mAdapter.remove(position);
        }
    }
}
