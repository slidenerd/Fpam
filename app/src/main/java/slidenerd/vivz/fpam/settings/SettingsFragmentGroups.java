package slidenerd.vivz.fpam.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.SettingsGroupsAdapter;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.settings_groups)
public class SettingsFragmentGroups extends Fragment {

    @ViewById(R.id.text_empty)
    TextView mTextEmpty;
    @ViewById(R.id.recycler_groups)
    RecyclerView mRecyclerGroups;
    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void onViewCreated() {
        mRecyclerGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerGroups.setHasFixedSize(true);
        RealmResults<Group> results = mRealm.where(Group.class).findAllSorted("name");
        mRecyclerGroups.setAdapter(new SettingsGroupsAdapter(getActivity(), mRealm, results));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
