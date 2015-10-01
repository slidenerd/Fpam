package slidenerd.vivz.fpam.settings;

import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import slidenerd.vivz.fpam.R;

/**
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.frag_groups)
public class PreFragGroups extends Fragment {

    @ViewById(R.id.text_empty)
    TextView mTextEmpty;
    @ViewById(R.id.recycler_groups)
    RecyclerView mRecyclerGroups;

    @AfterViews
    void onViewCreated() {
        mRecyclerGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerGroups.setHasFixedSize(true);
    }
}
