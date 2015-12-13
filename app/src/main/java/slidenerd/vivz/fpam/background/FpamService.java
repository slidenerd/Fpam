package slidenerd.vivz.fpam.background;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import br.com.goncalves.pugnotification.notification.PugNotification;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.ui.ActivityLogin_;
import slidenerd.vivz.fpam.util.FBUtils;

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
        if (!FBUtils.canPublish(AccessToken.getCurrentAccessToken())) {
            LoginManager.getInstance().logOut();
            PugNotification.with(this)
                    .load()
                    .title("Login Required")
                    .message("Our ticket with Facebook has expired, can you please get us a new ticket?")
                    .smallIcon(R.drawable.pugnotification_ic_launcher)
                    .largeIcon(R.drawable.pugnotification_ic_launcher)
                    .autoCancel(true)
                    .click(ActivityLogin_.class)
                    .flags(Notification.DEFAULT_ALL)
                    .simple()
                    .build();
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }
}