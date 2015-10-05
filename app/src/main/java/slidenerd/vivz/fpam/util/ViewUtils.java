package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.res.TypedArray;

import slidenerd.vivz.fpam.R;

/**
 * Created by vivz on 05/10/15.
 */
public class ViewUtils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }
}
