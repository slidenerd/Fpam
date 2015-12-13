package slidenerd.vivz.fpam.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import slidenerd.vivz.fpam.R;

import static slidenerd.vivz.fpam.extras.Constants.TEXT_REQUEST_PERMISSION;

/**
 * Created by vivz on 04/12/15.
 */
@EFragment(R.layout.fragment_permission)
public class FragmentPermission extends DialogFragment {

    @FragmentArg(TEXT_REQUEST_PERMISSION)
    ArrayList<String> mMessage = new ArrayList<>();

    ActivityLogin mActivity;

    @ViewById(R.id.text_request_permission)
    TextView mTextRequestPermission;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ActivityLogin) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @AfterViews
    public void onViewCreated() {
        mTextRequestPermission.setText("You are missing " + mMessage);
    }

    @Click(R.id.btn_accept)
    public void onAccept() {
        dismiss();
        mActivity.performLogin(mMessage);
    }
}
