package slidenerd.vivz.fpam.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;

/**
 * Created by vivz on 18/09/15.
 */
public class DateUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static final long getUTCTimestamp(long milliseconds) {
        return milliseconds / 1000L;
    }

    public static final long getFBFormattedTime(String timeString) {
        long timeMillis = Constants.NA;
        try {
            timeMillis = format.parse(timeString).getTime();
        } catch (ParseException e) {
            L.m(timeString + " " + e);
        }
        return timeMillis;
    }

    public static final String getDurationHHMMSS(long duration) {
        String message = (String.format("%d hr %d min, %d sec", duration / (1000 * 60 * 60), (duration % (1000 * 60 * 60)) / (1000 * 60), ((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
        return message;
    }
}
