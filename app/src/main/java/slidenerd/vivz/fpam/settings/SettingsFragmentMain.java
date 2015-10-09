package slidenerd.vivz.fpam.settings;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import slidenerd.vivz.fpam.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragmentMain extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_monitor_frequency))) {
            int value = Integer.parseInt(sharedPreferences.getString(key, "-1"));
            if (value == -1) {
                getPreferenceScreen().findPreference(getString(R.string.key_groups)).setEnabled(false);
            } else {
                getPreferenceScreen().findPreference(getString(R.string.key_groups)).setEnabled(true);
            }
        }
    }
}