package slidenerd.vivz.fpam.background;

import android.app.IntentService;
import android.content.Intent;

public class FpamService extends IntentService {
    // Must create a default constructor
    public FpamService() {
        // Used to name the worker thread, important only for debugging.
        super("Facebook Spam Removal Service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
    }
}