package slidenerd.vivz.fpam.util;

import android.os.Build;

public class VersionUtils {
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