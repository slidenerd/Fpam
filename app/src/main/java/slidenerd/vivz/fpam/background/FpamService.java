package slidenerd.vivz.fpam.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;

import static slidenerd.vivz.fpam.extras.Constants.TAG;

public class FpamService extends IntentService {


    //https://github.com/commonsguy/cw-advandroid/tree/master/SystemServices/Alarm/src/com/commonsware/android/syssvc/alarm
    // Must create a default constructor
    public FpamService() {
        // Used to name the worker thread, important only for debugging.
        super("Facebook Spam Removal Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        Log.i(TAG, "onHandleIntent: " + AccessToken.getCurrentAccessToken());
        AlarmReceiver.completeWakefulIntent(intent);
    }
}