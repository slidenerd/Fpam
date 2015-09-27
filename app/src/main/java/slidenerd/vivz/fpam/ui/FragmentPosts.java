package slidenerd.vivz.fpam.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.PostAdapter;
import slidenerd.vivz.fpam.widget.RecyclerViewEmptySupport;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_posts)
public class FragmentPosts extends Fragment {

    @ViewById(R.id.recycler_posts)
    RecyclerViewEmptySupport mRecyclerPosts;
    @ViewById(R.id.text_empty_posts)
    TextView mTextEmpty;
    private Realm mRealm;

    public FragmentPosts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    public void onViewCreated() {
        mRecyclerPosts.setEmptyView(mTextEmpty);
        mRecyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPosts.setAdapter(new PostAdapter(getActivity(), mRealm));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
