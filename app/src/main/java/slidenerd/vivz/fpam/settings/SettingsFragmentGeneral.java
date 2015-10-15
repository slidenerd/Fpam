package slidenerd.vivz.fpam.settings;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import slidenerd.vivz.fpam.R;

/**
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.settings_general)
public class SettingsFragmentGeneral extends Fragment {
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @AfterViews
    void onViewCreated() {

    }

}
