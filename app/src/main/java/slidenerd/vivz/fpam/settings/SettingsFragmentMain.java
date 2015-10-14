package slidenerd.vivz.fpam.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.annotations.PreferenceScreen;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.extras.Constants;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)

@EFragment
@PreferenceScreen(R.xml.pref_main)
public class SettingsFragmentMain extends PreferenceFragment {

    public static String PREF_NAME = "MyPrefs";
    @PreferenceByKey(R.string.key_groups)
    Preference mPrefGroups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(PREF_NAME);
    }

    @PreferenceClick(R.string.key_groups)
    public void onClickGroups() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, new SettingsFragmentGroups_())
                .addToBackStack(getString(R.string.key_groups))
                .commit();
    }

    @PreferenceClick(R.string.key_spammers)
    public void onClickSpammers() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, new SettingsFragmentSpammers_())
                .addToBackStack(getString(R.string.key_groups))
                .commit();
    }

    @PreferenceChange(R.string.key_monitor_frequency)
    public void onPreferenceMonitorFrequencyChanged(Preference preference, String newValue) {
        //Disable the option to choose which groups to monitor if the user has selected a frequency of never in the frequency settings.
        int value = Integer.parseInt(newValue);
        if (value == Constants.NA) {
            mPrefGroups.setEnabled(false);
        } else {
            mPrefGroups.setEnabled(true);
        }
    }
}