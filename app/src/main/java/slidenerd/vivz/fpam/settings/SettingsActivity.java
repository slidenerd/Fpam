package slidenerd.vivz.fpam.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import slidenerd.vivz.fpam.R;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    @ViewById(R.id.app_bar)
    Toolbar mToolbar;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /*
    Note: We have not added the menu_base fragment to the back stack, which means on pressing back both the ActivityBase Fragment and SettingsActivity Activity will exit together
     */

    /**
     * Returns true if the app is running on an extra large screen of a tablet.
     */
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new SettingsFragmentMain())
                    .commit();
        }
    }

    @AfterViews
    void afterView() {
        mToolbar.setTitle(R.string.action_settings);
        setSupportActionBar(mToolbar);
    }

    /*
    If we have more than 0 items on our back stack, it means one of the Preference Fragments was added to our back stack in which case, we pop the topmost item containing that Fragment which exposes the root SettingsActivity screen once again.
     */
    @Override
    public void onBackPressed() {
        int backStackCount = getFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem(android.R.id.home)
    void onHomeSelected() {
        onBackPressed();
    }
}
