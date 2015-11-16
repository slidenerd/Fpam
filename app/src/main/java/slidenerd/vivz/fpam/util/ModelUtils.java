package slidenerd.vivz.fpam.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vivz on 06/10/15.
 */
public class ModelUtils {

    public static String computeSpammerId(String userId, String groupId) {
        return userId + ":" + groupId;
    }

    public static String computeDailyticsId(String groupId) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String analyticsDate;
        if (currentTime > 0) {
            analyticsDate = dateFormat.format(new Date(currentTime));
        } else {
            analyticsDate = "NA";
        }
        return groupId + ":" + analyticsDate;
    }
}
