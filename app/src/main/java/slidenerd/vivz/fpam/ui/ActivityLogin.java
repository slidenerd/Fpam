package slidenerd.vivz.fpam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups_;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.util.FBUtils;

/**

 */
@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity implements TaskLoadAdminAndGroups.TaskCallback {
    private static final String TAG = "task_load_admin_groups";
    @App
    Fpam mApp;
    @ViewById(R.id.progress)
    ProgressBar mProgress;

    @ViewById(R.id.text_facebook_error)
    TextView mTextError;
    private TaskLoadAdminAndGroups_ mTask;
    private CallbackManager mCallbackManager;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken token = loginResult.getAccessToken();
            //if we have an access token which is neither null nor expired, has the permission to read email of the person logging in and groups, then we jump into the app
            mTextError.setVisibility(View.GONE);
            mApp.setToken(token);
            if (FBUtils.canRead(token)) {
                mProgress.setVisibility(View.VISIBLE);
                mTask.loadAdminAndGroupsInBackground(token);
            }

            //redirect the person to the login screen after displaying a dialog that shows why and how the permissions are going to be used

            else {
                new MaterialDialog.Builder(ActivityLogin.this)
                        .title(R.string.text_permission_declined)
                        .customView(R.layout.request_permission, true)
                        .autoDismiss(false)
                        .cancelable(false)
                        .positiveText(R.string.ok)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                performLogin();
                            }
                        }).show();
            }
        }

        @Override
        public void onCancel() {
            L.m("You cancelled");
        }

        @Override
        public void onError(FacebookException error) {
            L.m("Error " + error.toString());
            mTextError.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mTask = (TaskLoadAdminAndGroups_) getSupportFragmentManager().findFragmentByTag(TAG);
        if (mTask == null) {
            mTask = new TaskLoadAdminAndGroups_();
            getSupportFragmentManager().beginTransaction().add(mTask, TAG).commit();
        }
    }

    @Click(R.id.btn_login)
    public void login() {
        performLogin();
    }

    private void performLogin() {
        String[] permissions = Constants.READ_PERMISSIONS;
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, mCallback);
        loginManager.logInWithReadPermissions(this, Arrays.asList(permissions));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void afterAdminAndGroupsLoaded() {
        mProgress.setVisibility(View.GONE);
        ActivityMain_.intent(this).start();
        finish();
    }
}