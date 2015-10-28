package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Windows on 01-03-2015.
 */
public class DisplayUtils {
    public static int getWidthPixels(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeightPixels(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(float dp, Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(float px, Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static int getResolvedColor(Context context, int color) {
        int resolvedColor;
        if (VersionUtils.isMarshmallowOrMore()) {
            resolvedColor = context.getResources().getColor(color, context.getTheme());
        } else {
            resolvedColor = context.getResources().getColor(color);
        }
        return resolvedColor;
    }

    public static Point getPostImageSize(Context context) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = Math.round(width * 9.0F / 16.0F);
        return new Point(width, height);
    }
}