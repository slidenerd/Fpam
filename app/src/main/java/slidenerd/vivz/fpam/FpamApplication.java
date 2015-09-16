package slidenerd.vivz.fpam;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import org.androidannotations.annotations.EApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by vivz on 28/07/15.
 */
@EApplication
public class FpamApplication extends Application {

    public static AccessToken getFacebookAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setIsDebugEnabled(true);
    }
}
