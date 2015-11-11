package slidenerd.vivz.fpam.util;

import android.support.v7.widget.RecyclerView;

/**
 * Created by vivz on 06/10/15.
 */
public class ModelUtils {

    public static String computeSpammerId(String userId, String groupId) {
        return userId + ":" + groupId;
    }

    public static long computeRowId(String postId) {
        long rowId = RecyclerView.NO_ID;
        if (postId != null && !postId.trim().isEmpty()) {
            int underscore = postId.indexOf("_");
            if (underscore > -1) {
                String suffix = postId.substring(underscore + 1, postId.length());
                if (!suffix.trim().isEmpty()) {
                    try {
                        rowId = Long.parseLong(suffix);
                    } catch (ArithmeticException ignore) {

                    }
                }
            }
        }
        return rowId;
    }

}
