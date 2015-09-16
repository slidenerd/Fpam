package slidenerd.vivz.fpam.log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import slidenerd.vivz.fpam.BuildConfig;

/**
 * Created by Windows on 13-01-2015.
 */
public class L {

    public static void m(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("VIVZ", "" + message);
        }
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }

    public static void T(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }
}
