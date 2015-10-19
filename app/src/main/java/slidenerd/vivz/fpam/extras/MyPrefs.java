package slidenerd.vivz.fpam.extras;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean hasSeenDrawer();

    @DefaultInt(value = Constants.DEFAULT_NUMBER_OF_ITEMS_TO_FETCH)
    int cacheSize();

    @DefaultInt(value = -1)
    int scanFrequency();

    @DefaultBoolean(false)
    boolean swipeToDelete();

}