package slidenerd.vivz.fpam.util;

import android.support.v7.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vivz on 06/10/15.
 */
public class ModelUtils {

    public static String computeGroupId(String postId) {
        return postId.substring(0, postId.indexOf("_"));
    }

    public static String computeSpammerId(String userId, String groupId) {
        return userId + ":" + groupId;
    }

    public static String computeDailyticsId(String groupId, String date) {
        return groupId + ":" + date;
    }
    /*
    Used to get the item id from a post id which can be used in the getItemId method of the Adapter which is responsible for displaying posts.
     */

    public static long computePostItemId(String postId) {
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

    //2015-11-12T05:07:44+0000

    public static String computeAnalyticsDate(String fbFormattedDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Date date = dateFormat.parse(fbFormattedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return day + "-" + month + "-" + year;
    }

    public static String computeAnalyticsDate(long dateTime) {
        String analyticsDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (dateTime > 0) {
            analyticsDate = dateFormat.format(new Date(dateTime));
        } else {
            analyticsDate = "NA";
        }
        return analyticsDate;
    }

}
