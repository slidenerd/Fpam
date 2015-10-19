package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class VersionUtils {


    public static int getVersionCode(Context context) {
        PackageInfo manager = null;
        try {
            manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return manager.versionCode;
    }


    public static String getVersionName(Context context) {
        PackageInfo manager = null;
        try {
            manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return manager.versionName;
    }

    public static boolean isJellyBeanOrMore() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean isLollipopOrMore() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isIceCreamSandwichOrMore() {
        return Build.VERSION.SDK_INT >= 14;
    }

    public static boolean isMarshmallowOrMore() {
        return Build.VERSION.SDK_INT >= 23;
    }
}