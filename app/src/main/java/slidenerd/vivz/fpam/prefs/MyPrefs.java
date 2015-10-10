package slidenerd.vivz.fpam.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.extras.Constants;

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean hasSeenDrawer();

    @DefaultInt(value = Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH, keyRes = R.string.key_post_cache_size)
    int cacheSize();

    @DefaultString(value = "Every 2 hours", keyRes = R.string.key_monitor_frequency)
    String monitorSpam();

}