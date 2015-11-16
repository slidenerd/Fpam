package slidenerd.vivz.fpam.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import slidenerd.vivz.fpam.R;

/**
 * Created by vivz on 16/11/15.
 */

public class FragmentProgress extends DialogFragment {
    public static void show(AppCompatActivity context) {
        FragmentProgress dialog = new FragmentProgress();
        dialog.show(context.getSupportFragmentManager(), "[ABOUT_DIALOG]");
    }

    public static void dismiss(AppCompatActivity context) {
        FragmentProgress fragment = (FragmentProgress) context.getSupportFragmentManager().findFragmentByTag("[ABOUT_DIALOG]");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .content(R.string.loading)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
