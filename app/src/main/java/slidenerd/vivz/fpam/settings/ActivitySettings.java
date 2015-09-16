package slidenerd.vivz.fpam.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import slidenerd.vivz.fpam.R;

public class ActivitySettings extends AppCompatActivity {

    private Toolbar mToolbar;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /*
    Note: We have not added the main fragment to the back stack, which means on pressing back both the Main Fragment and ActivitySettings Activity will exit together
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
        setContentView(R.layout.activity_settings);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle(R.string.action_settings);
        setSupportActionBar(mToolbar);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new PreFragMain())
                    .commit();
        }
    }

    /*
    If we have more than 0 items on our back stack, it means one of the Preference Fragments was added to our back stack in which case, we pop the topmost item containing that Fragment which exposes the root ActivitySettings screen once again.
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
