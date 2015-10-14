package slidenerd.vivz.fpam.settings;

import android.support.v4.app.Fragment;
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
@EFragment(R.layout.settings_spammers)
public class SettingsFragmentGeneral extends Fragment {

    @ViewById(R.id.text_empty)
    TextView mTextEmpty;
    @ViewById(R.id.recycler_spammers)
    RecyclerView mRecyclerSpammers;

    @AfterViews
    void onViewCreated() {
        mRecyclerSpammers.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerSpammers.setHasFixedSize(true);
    }
}
