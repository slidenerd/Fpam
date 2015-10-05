package slidenerd.vivz.fpam.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import slidenerd.vivz.fpam.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragmentMain extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_groups))) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new SettingsFragmentGroups_())
                    .addToBackStack(getString(R.string.key_groups))
                    .commit();
        }
        if (preference.getKey().equals(getString(R.string.key_spammers))) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new SettingsFragmentSpammers_())
                    .addToBackStack(getString(R.string.key_groups))
                    .commit();
        }
        return false;
    }
}