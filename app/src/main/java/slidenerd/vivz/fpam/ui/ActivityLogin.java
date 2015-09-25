package slidenerd.vivz.fpam.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

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
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

import io.realm.Realm;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Groups;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.JSONUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity implements FacebookCallback<LoginResult> {
    @ViewById(R.id.progress)
    ProgressBar mProgress;
    private CallbackManager mCallbackManager;
    private AlertDialog mDialog;

    private void showDialogJustifyingPermissions(AccessToken accessToken) {
        Set<String> setDeclinedPermissions = accessToken.getDeclinedPermissions();
        if (!setDeclinedPermissions.isEmpty()) {
            mDialog = new AlertDialog.Builder(this)
                    .setTitle("Fpam would love your approval")
                    .setMessage("Fpam wants the following permissions from you to work \n Email: Needed to maintain your spam settings \n Groups: Needed to access a list of groups you own so that Fpam can manage them. Fpam won't post in any group without asking you. \n Friends: Fpam needs this to keep everything in sync between you and your buddy admins. Fpam won't message your friends or post on their timeline without asking you.")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            initLoginManager();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            initLoginManager();
                        }
                    })
                    .create();
            if (mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }
        } else {
            if (FBUtils.isValidToken(accessToken)) {
                mProgress.setVisibility(View.VISIBLE);
                loadUserAndGroupsAsync(accessToken);
            } else {
                L.m("access token is null or expired");
            }
        }
    }

    @Background
    void loadUserAndGroupsAsync(AccessToken accessToken) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            JSONObject adminObject = FBUtils.requestMeSync(accessToken);
            JSONObject groupsObject = FBUtils.requestGroupsSync(accessToken);
            //Store the admin and list of groups associated with the admin on the UI Thread
            Admin admin = JSONUtils.loadAdminFrom(adminObject);
            Groups groups = JSONUtils.loadGroupsFrom(groupsObject);
            DataStore.storeAdmin(realm, admin);
            DataStore.storeGroups(realm, groups);
        } catch (JSONException e) {
            L.m("" + e);
        } finally {
            if (realm != null) {
                realm.close();
            }
            onUserAndGroupsLoaded();
        }

    }

    @UiThread
    void onUserAndGroupsLoaded() {
        mProgress.setVisibility(View.GONE);
        NavUtils.startActivityStats(ActivityLogin.this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
    }

    /**
     * Create the Login Manager responsible for facebook login and login with the initial read permissions.
     */
    @Click(R.id.btn_login)
    public void onClickLogin() {
        initLoginManager();
    }

    private void initLoginManager() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, this);
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "user_managed_groups", "user_friends"));
    }


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}