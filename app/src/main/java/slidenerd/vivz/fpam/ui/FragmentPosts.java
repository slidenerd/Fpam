package slidenerd.vivz.fpam.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.parceler.Parcels;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.PostAdapter;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.widget.RecyclerViewEmptySupport;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentPosts extends Fragment {

    @InstanceState
    String mGroupId = Constants.GROUP_ID_NONE;
    private RecyclerViewEmptySupport mRecyclerPosts;
    private TextView mTextEmpty;
    private PostAdapter mAdapter;
    private Realm mRealm;
    private BroadcastReceiver mGroupSelectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Group selectedGroup = Parcels.unwrap(intent.getExtras().getParcelable("selectedGroup"));
            RealmResults<Post> realmResults = mRealm.where(Post.class).beginsWith("postId", selectedGroup.getId()).findAll();
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
        mTextEmpty = (TextView) view.findViewById(R.id.text_empty_posts);
        mRecyclerPosts = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_posts);
        mRecyclerPosts.setEmptyView(mTextEmpty);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPosts.setAdapter(mAdapter);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mGroupSelectedReceiver, new IntentFilter("group_selected"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mGroupSelectedReceiver);
    }
}
