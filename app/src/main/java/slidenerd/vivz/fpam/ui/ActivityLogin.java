package slidenerd.vivz.fpam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups;
import slidenerd.vivz.fpam.background.TaskLoadAdminAndGroups_;
import slidenerd.vivz.fpam.util.FBUtils;

import static slidenerd.vivz.fpam.extras.Constants.PERMISSION_EMAIL;
import static slidenerd.vivz.fpam.extras.Constants.PERMISSION_GROUPS;
import static slidenerd.vivz.fpam.extras.Constants.READ_PERMISSIONS;

/**

 */
@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.menu_activity_login)
public class ActivityLogin extends AppCompatActivity implements TaskLoadAdminAndGroups.TaskCallback {
    private static final String TAG = "task_load_admin_groups";
    @App
    Fpam mApp;

    @ViewById(R.id.root)
    View root;
    @ViewById(R.id.progress)
    ProgressBar mProgress;

    @ViewById(R.id.error)
    TextView mTextError;
    private TaskLoadAdminAndGroups_ mTask;
    private CallbackManager mCallbackManager;


    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            //TODO when you clear all permissions from facebook console, first give no permission, then give only email, then give only groups, it still assumes that all permissions are not available since getRecentlyDeclinedPermissions returns groups in the last round and hence find a way to make a better dialog
            AccessToken token = loginResult.getAccessToken();
            //if we have an access token which is neither null nor expired, has the permission to read email of the person logging in and groups, then we jump into the app
            mApp.setToken(token);
            FragmentPermission fragment = null;
            if (FBUtils.canRead(token)) {
                mProgress.setVisibility(View.VISIBLE);
                mTask.loadAdminAndGroupsInBackground(token);
                mTextError.setVisibility(View.GONE);
            } else {
                Set<String> denied = loginResult.getRecentlyDeniedPermissions();
                if (denied.contains(PERMISSION_EMAIL) && denied.contains(PERMISSION_GROUPS)) {
                    fragment = FragmentPermission_.builder().mMessage(new ArrayList<>(Arrays.asList(PERMISSION_EMAIL, PERMISSION_GROUPS))).build();
                    fragment.show(getSupportFragmentManager(), "permission");

                } else if (denied.contains(PERMISSION_EMAIL)) {
                    fragment = FragmentPermission_.builder().mMessage(new ArrayList<>(Arrays.asList(PERMISSION_EMAIL))).build();
                    fragment.show(getSupportFragmentManager(), "permission");

                } else if (denied.contains(PERMISSION_GROUPS)) {
                    fragment = FragmentPermission_.builder().mMessage(new ArrayList<>(Arrays.asList(PERMISSION_GROUPS))).build();
                    fragment.show(getSupportFragmentManager(), "permission");
                }
            }

        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel: ");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, "onError: ", error);
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
        performLogin(READ_PERMISSIONS);
    }

    public void performLogin(List<String> permissions) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, mCallback);
        loginManager.logInWithReadPermissions(this, permissions);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void afterAdminLoaded(FacebookRequestError error) {
        if (error != null) {
            Log.i(TAG, "afterAdminLoaded: " + error);
        }
    }

    @Override
    public void afterGroupsLoaded(FacebookRequestError error) {
        mProgress.setVisibility(View.GONE);
        if (error != null) {
            Log.i(TAG, "afterGroupsLoaded: " + error);
        }
        ActivityMain_.intent(this).start();
        finish();
    }
}