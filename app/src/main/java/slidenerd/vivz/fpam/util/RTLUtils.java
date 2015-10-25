package slidenerd.vivz.fpam.util;

import android.support.v4.view.ViewCompat;
import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RTLUtils {

    private static final Set<String> RTL;

    static {
        Set<String> lang = new HashSet<String>();
        lang.add("ar"); // Arabic
        lang.add("dv"); // Divehi
        lang.add("fa"); // Persian (Farsi)
        lang.add("ha"); // Hausa
        lang.add("he"); // Hebrew
        lang.add("iw"); // Hebrew (old code)
        lang.add("ji"); // Yiddish (old code)
        lang.add("ps"); // Pashto, Pushto
        lang.add("ur"); // Urdu
        lang.add("yi"); // Yiddish
        RTL = Collections.unmodifiableSet(lang);
    }

    public static boolean isRTL(Locale locale) {
        if (locale == null)
            return false;

        // Character.getDirectionality(locale.getDisplayName().charAt(0))
        // can lead to NPE (Java 7 bug)
        // https://bugs.openjdk.java.net/browse/JDK-6992272?page=com.atlassian.streams.streams-jira-plugin:activity-stream-issue-tab
        // using hard coded list of locale instead
        return RTL.contains(locale.getLanguage());
    }

    public static boolean isRTL(View view) {
        if (view == null)
            return false;

        // config.getLayoutDirection() only available since 4.2
        // -> using ViewCompat instead (from Android support library)
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            return true;
        }
        return false;
    }
}