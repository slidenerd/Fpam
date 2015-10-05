package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;

import slidenerd.vivz.fpam.settings.SettingsActivity_;
import slidenerd.vivz.fpam.ui.ActivityCache_;
import slidenerd.vivz.fpam.ui.ActivityLogin_;
import slidenerd.vivz.fpam.ui.Child_;

/**
 * Created by vivz on 28/07/15.
 */
public class NavUtils {
    public static void startActivityLogin(Context context) {
        ActivityLogin_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

    public static void startActivityCache(Context context) {
        ActivityCache_.intent(context).start();
    }

    public static void startActivitySettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity_.class);
        context.startActivity(intent);
    }

    public static void startActivityChild(Context context) {
        Child_.intent(context).start();
    }
}
