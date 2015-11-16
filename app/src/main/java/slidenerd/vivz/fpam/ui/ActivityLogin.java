package slidenerd.vivz.fpam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

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
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups_;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.util.FBUtils;

/**
 * TODO make a better dialog to ask permissions and handle onCancel and onError in a better manner
 */
@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity implements TaskLoadAdminAndGroups.TaskCallback {
    private static final String TAG_FRAGMENT = "task_fragment";
    @App
    Fpam mApplication;
    @ViewById(R.id.progress)
    ProgressBar mProgress;
    private TaskLoadAdminAndGroups_ mTaskFragment;
    private CallbackManager mCallbackManager;


    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken token = loginResult.getAccessToken();
            //if we have an access token which is neither null nor expired, has the permission to read email of the person logging in and groups, then we jump into the app

            mApplication.setToken(token);
            if (FBUtils.isValidAndCanReadEmailGroups(token)) {
                mProgress.setVisibility(View.VISIBLE);
                mTaskFragment.loadAdminAndGroupsInBackground(token);
            }

            //redirect the person to the login screen after displaying a dialog that shows why and how the permissions are going to be used

            else {
                new MaterialDialog.Builder(ActivityLogin.this)
                        .title(R.string.text_permission_declined)
                        .customView(R.layout.request_permission, true)
                        .autoDismiss(false)
                        .cancelable(false)
                        .positiveText(R.string.text_ok)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                initLoginManager();
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
            L.m("Error " + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mTaskFragment = (TaskLoadAdminAndGroups_) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskLoadAdminAndGroups_();
            getSupportFragmentManager().beginTransaction().add(mTaskFragment, TAG_FRAGMENT).commit();
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
        String[] readPermissions = Constants.READ_PERMISSIONS;
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, mFacebookCallback);
        loginManager.logInWithReadPermissions(this, Arrays.asList(readPermissions));
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