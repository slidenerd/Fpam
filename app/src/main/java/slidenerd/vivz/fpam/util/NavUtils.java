package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;

import slidenerd.vivz.fpam.settings.ActivitySettings_;
import slidenerd.vivz.fpam.ui.ActivityCache_;
import slidenerd.vivz.fpam.ui.ActivityLogin_;
import slidenerd.vivz.fpam.ui.ActivityMain_;
import slidenerd.vivz.fpam.settings.ActivitySettings;
import slidenerd.vivz.fpam.ui.ActivityLogin_;

/**
 * Created by vivz on 28/07/15.
 */
public class NavUtils {
    public static void startActivityLogin(Context context) {
        ActivityLogin_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

    public static void startActivityStats(Context context) {
        ActivityMain_.intent(context).start();
    }

    public static void startActivityCache(Context context) {
        ActivityCache_.intent(context).start();
    }

    public static void startActivitySettings(Context context) {
        Intent intent = new Intent(context, ActivitySettings_.class);
        context.startActivity(intent);
    }
}
