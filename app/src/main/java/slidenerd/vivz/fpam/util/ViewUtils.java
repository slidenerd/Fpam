package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

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

    public static void expand(final View v, final int initialHeight) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = initialHeight;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                L.m("interpolated time " + interpolatedTime);
                v.getLayoutParams().height = (int) (initialHeight + (targetHeight - initialHeight) * interpolatedTime);
                if (interpolatedTime < 0.1 || interpolatedTime > 0.9 || interpolatedTime > 0.45 && interpolatedTime < 0.55) {
                    v.requestLayout();
                }

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(200);
        v.startAnimation(a);

    }

    public static void collapse(final View v, final int initialHeight) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = initialHeight - (int) ((initialHeight - targetHeight) * interpolatedTime);
                if (interpolatedTime < 0.1 || interpolatedTime > 0.9 || interpolatedTime > 0.45 && interpolatedTime < 0.55) {
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(200);
        v.startAnimation(a);
    }
}
