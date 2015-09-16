package slidenerd.vivz.fpam.util;

import android.widget.EditText;

/**
 * Created by vivz on 06/08/15.
 */
public class ValidationUtils {
    public static boolean hasInput(EditText editText) {
        return editText != null
                && editText.getText() != null
                && editText.getText().toString() != null
                && !editText.getText().toString().trim().isEmpty() ? true : false;
    }
}
