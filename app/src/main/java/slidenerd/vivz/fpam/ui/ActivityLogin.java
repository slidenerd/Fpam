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

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;
import java.util.Set;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity implements FacebookCallback<LoginResult>, TaskFragmentLogin.TaskCallback {
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    @ViewById(R.id.progress)
    ProgressBar mProgress;
    private TaskFragmentLogin_ mTaskFragment;
    private CallbackManager mCallbackManager;
    private AlertDialog mDialog;

    private void showDialogJustifyingPermissions(AccessToken accessToken) {
        Set<String> setDeclinedPermissions = accessToken.getDeclinedPermissions();
        if (!setDeclinedPermissions.isEmpty()) {
            mDialog = new AlertDialog.Builder(this)
                    .setTitle("Fpam would love your approval")
                    .setMessage("Fpam wants the following permissions from you to work \n Email: Needed to maintain your spam settings \n GroupFields: Needed to access a list of groups you own so that Fpam can manage them. Fpam won't post in any group without asking you. \n Friends: Fpam needs this to keep everything in sync between you and your buddy admins. Fpam won't message your friends or post on their timeline without asking you.")
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
                mTaskFragment.loadUserAndGroupsAsync(accessToken);
            } else {
                L.m("access token is null or expired");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mTaskFragment = (TaskFragmentLogin_) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragmentLogin_();
            getSupportFragmentManager().beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
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
        L.t(ActivityLogin.this, "Couldn't connect with Facebook Servers " + e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUserAndGroupsLoaded() {
        mProgress.setVisibility(View.GONE);
        NavUtils.startActivityChild(ActivityLogin.this);
        finish();
    }
}