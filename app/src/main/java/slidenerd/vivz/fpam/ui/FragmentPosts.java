package slidenerd.vivz.fpam.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import org.parceler.Parcels;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FlipInLeftYAnimator;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.AdapterPost;
import slidenerd.vivz.fpam.adapter.OnItemClickListener;
import slidenerd.vivz.fpam.adapter.RecyclerViewHelperImpl;
import slidenerd.vivz.fpam.adapter.SwipeToDismissTouchListener;
import slidenerd.vivz.fpam.adapter.SwipeableItemClickListener;
import slidenerd.vivz.fpam.core.Core;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;


/**
 * TODO handle error conditions, handle the loginmanager properly, how the items are shown while deleting and after deleting, move the background activities to a retained fragment, move the scroll position to wherever the user was previously after restoring adapter on delete
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentPosts extends Fragment implements FacebookCallback<LoginResult> {

    private static final String STATE_SELECTED_GROUP = "group";
    @App
    Fpam mApplication;
    @InstanceState
    String mGroupId = Constants.GROUP_ID_NONE;
    private RecyclerView mRecyclerPosts;
    private AdapterPost mAdapter;
    private Realm mRealm;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    private Group mSelectedGroup;
    private RealmResults<Post> mResults;
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
            mSelectedGroup = Parcels.unwrap(savedInstanceState.getParcelable(STATE_SELECTED_GROUP));
        }
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
        mResults = mRealm.where(Post.class).equalTo("postId", "NONE").findAll();
        mRecyclerPosts = (RecyclerView) view.findViewById(R.id.recycler_posts);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(mContext));
        FlipInLeftYAnimator animator = new FlipInLeftYAnimator();
        mRecyclerPosts.setItemAnimator(animator);
        mAdapter = new AdapterPost(mContext, mRealm, mResults);
        mAdapter.setHasStableIds(true);
        mRecyclerPosts.setAdapter(mAdapter);
        final SwipeToDismissTouchListener<RecyclerViewHelperImpl> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewHelperImpl(mRecyclerPosts),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewHelperImpl>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewHelperImpl view, int position) {
                                NavUtils.broadcastDeletePost(mContext, position, mAdapter.getItem(position));
                            }
                        });


        mRecyclerPosts.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerPosts.addOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecyclerPosts.addOnItemTouchListener(new SwipeableItemClickListener(mContext,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data

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


    @Receiver(actions = Constants.ACTION_LOAD_FEED, registerAt = Receiver.RegisterAt.OnCreateOnDestroy, local = true)
    public void onBroadcastSelectedGroup(Context context, Intent intent) {
        mSelectedGroup = Parcels.unwrap(intent.getExtras().getParcelable(NavUtils.EXTRA_SELECTED_GROUP));
        Core core = new Core();
        String deletes = Keyword.toString(core.getRelevantKeywords(mRealm, mSelectedGroup.getGroupId()));
        L.m(deletes);
        mResults = mRealm.where(Post.class).beginsWith("postId", mSelectedGroup.getGroupId()).findAllSortedAsync("updatedTime", false);
        mResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateRealmResults(mResults);
            }
        });
    }
}
