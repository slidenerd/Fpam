package slidenerd.vivz.fpam.extras;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import slidenerd.vivz.fpam.R;

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface MyPrefs {

    @DefaultBoolean(value = false)
    boolean hasSeenDrawer();

    @DefaultInt(value = Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH, keyRes = R.string.key_cache_size)
    int cacheSize();

    @DefaultInt(value = -1, keyRes = R.string.key_scan_frequency)
    int scanFrequency();

    @DefaultBoolean(value = false, keyRes = R.string.key_swipe_to_delete)
    boolean swipeToDelete();
}