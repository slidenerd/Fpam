package slidenerd.vivz.fpam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import io.realm.Realm;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.FBAdmin;
import slidenerd.vivz.fpam.model.json.group.FBGroup;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private AlertDialog mDialog;
    private Realm realm;

    /**
     * This callback is invoked when the user has hit the login button and successfully logged in Facebook. The user is given an AccessToken which should not be null anymore. If the access token is valid, load the user profile and groups data in a background Async Task.
     */
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            showDialogJustifyingPermissions(accessToken);
        }

        @Override
        public void onCancel() {
            L.m("You cancelled while logging in");
        }

        @Override
        public void onError(FacebookException e) {
            L.t(ActivityLogin.this, "Facebook Servers couldn't connect to Fpam " + e);
        }
    };


    private void showDialogJustifyingPermissions(AccessToken accessToken) {
        Set<String> setDeclinedPermissions = accessToken.getDeclinedPermissions();
        if (!setDeclinedPermissions.isEmpty()) {
            mDialog = new AlertDialog.Builder(this)
                    .setTitle("Fpam would love your approval")
                    .setMessage("Fpam wants the following permissions from you to work \n Email: Needed to maintain your spam settings \n Groups: Needed to access a list of groups you own so that Fpam can manage them. Fpam won't post in any group without asking you. \n Friends: Fpam needs this to keep everything in sync between you and your buddy admins. Fpam won't message your friends or post on their timeline without asking you.")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setupLoginManager();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setupLoginManager();
                        }
                    })
                    .create();
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        } else {
            if (FBUtils.isValidToken(accessToken)) {
                loadUserAndGroups(accessToken);
            }
        }
    }

    @Background
    void loadUserAndGroups(AccessToken accessToken) {
        FBAdmin admin = null;
        ArrayList<FBGroup> listGroups = new ArrayList<>();
        try {
            admin = FBUtils.requestMeSync(accessToken);
            listGroups = FBUtils.requestGroupsSync(accessToken);
            Collections.sort(listGroups, new Comparator<FBGroup>() {
                @Override
                public int compare(FBGroup lhs, FBGroup rhs) {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                }
            });
        } catch (JSONException e) {
            L.m("" + e);
        }
        //Store the admin and list of groups associated with the admin on the UI Thread
        storeUserAndGroups(admin, listGroups);
    }

    @UiThread
    void storeUserAndGroups(FBAdmin admin, ArrayList<FBGroup> listGroups) {
        DataStore.storeAdmin(this, admin);
        DataStore.storeGroups(realm, this, listGroups);
        NavUtils.startActivityStats(ActivityLogin.this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(this);
        mCallbackManager = CallbackManager.Factory.create();
    }

    /**
     * Create the Login Manager responsible for facebook login and login with the initial read permissions.
     */
    @Click(R.id.btn_login)
    public void onClickLogin() {
        setupLoginManager();
    }

    private void setupLoginManager() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, mFacebookCallback);
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "user_managed_groups", "user_friends"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
