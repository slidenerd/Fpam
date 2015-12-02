package slidenerd.vivz.fpam.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntArrayRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.SettingsGroupsAdapter;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.model.json.Group;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_NAME;
import static slidenerd.vivz.fpam.extras.Constants.NA;

/**
 * TODO save the list of groups selected by the user
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.settings_groups)
public class SettingsFragmentGroups extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @Pref
    MyPrefs_ mPref;

    @ViewById(R.id.text_empty)
    TextView mTextEmpty;

    @ViewById(R.id.recycler_groups)
    RecyclerView mRecyclerGroups;

    @StringArrayRes(R.array.pref_scan_frequency_titles)
    String[] mScanFrequencyTitles;

    @IntArrayRes(R.array.pref_scan_frequency_values)
    int[] mScanFrequencyValues;

    private View mHeaderGroups;
    private TextView mTextScanFrequency;
    private TextView mTextSummaryScanFrequency;
    private Realm mRealm;
    private Context mContext;
    private SettingsGroupsAdapter mAdapter;

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
    }

    @AfterViews
    void onViewCreated() {
        final int selectedIndex = getSelectedIndex();
        mRecyclerGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerGroups.setHasFixedSize(true);
        final RealmResults<Group> results = mRealm.where(Group.class).findAllSortedAsync(GROUP_NAME);
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter = new SettingsGroupsAdapter(getActivity(), mRealm, results);
                mHeaderGroups = LayoutInflater.from(mContext).inflate(R.layout.header_groups, mRecyclerGroups, false);
                mAdapter.setHeaderView(mHeaderGroups);
                mRecyclerGroups.setAdapter(mAdapter);
                mAdapter.setEnabled(selectedIndex != 0);
                mTextScanFrequency = (TextView) mHeaderGroups.findViewById(R.id.text_scan_frequency);
                mTextSummaryScanFrequency = (TextView) mHeaderGroups.findViewById(R.id.text_summary_scan_frequency);
                mTextScanFrequency.setOnClickListener(SettingsFragmentGroups.this);
                mTextSummaryScanFrequency.setOnClickListener(SettingsFragmentGroups.this);
                String selectedSummary = mScanFrequencyTitles[selectedIndex];
                mTextSummaryScanFrequency.setText(selectedSummary);
                mPref.getSharedPreferences().registerOnSharedPreferenceChangeListener(SettingsFragmentGroups.this);
                results.removeChangeListeners();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int selectedIndex = getSelectedIndex();
        new MaterialDialog.Builder(mContext)
                .title(R.string.pref_scan_frequency)
                .items(R.array.pref_scan_frequency_titles)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {

                        //Store the new value of cache size selected by the user in the SharedPreferences

                        mPref.scanFrequency().put(mScanFrequencyValues[position]);

                        //Update the summary to reflect the new value of cache size selected by the user

                        mTextSummaryScanFrequency.setText(mScanFrequencyTitles[position]);
                        return true;
                    }
                })
                .build()
                .show();
    }

    private int getSelectedIndex() {
        int scanFrequency = mPref.scanFrequency().get();
        int selectedIndex = 0;
        for (int i = 0; i < mScanFrequencyValues.length; i++) {
            if (mScanFrequencyValues[i] == scanFrequency) {
                selectedIndex = i;
            }
        }
        return selectedIndex;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPref.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_scan_frequency))) {
            int newValue = mPref.scanFrequency().get();
            mAdapter.setEnabled(newValue != NA);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
