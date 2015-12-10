package slidenerd.vivz.fpam.background;

import android.app.IntentService;
import android.content.Intent;

import com.facebook.AccessToken;

import slidenerd.vivz.fpam.L;

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
        L.m("onHandleIntent was called");
        AlarmReceiver.completeWakefulIntent(intent);
        L.m("onHandleIntent " + AccessToken.getCurrentAccessToken());


    }
}