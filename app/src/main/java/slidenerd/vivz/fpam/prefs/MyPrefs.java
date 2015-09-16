package slidenerd.vivz.fpam.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean firstTime();

}