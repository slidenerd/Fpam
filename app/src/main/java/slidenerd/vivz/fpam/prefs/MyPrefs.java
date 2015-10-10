package slidenerd.vivz.fpam.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean hasSeenDrawer();

}