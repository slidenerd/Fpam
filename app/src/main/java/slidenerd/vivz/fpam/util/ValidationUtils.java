package slidenerd.vivz.fpam.util;

/**
 * Created by vivz on 26/10/15.
 */
public class ValidationUtils {
    public static boolean notNullOrEmpty(String string) {
        return string != null && !string.trim().isEmpty();
    }
}
