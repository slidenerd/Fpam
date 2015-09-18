package slidenerd.vivz.fpam.util;

/**
 * Created by vivz on 18/09/15.
 */
public class DateUtils {
    public static final long getUTCTimestamp() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }
}
