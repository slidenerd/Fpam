package slidenerd.vivz.fpam.settings;

import android.support.design.widget.TabLayout;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.ui.ActivityBase;

@EActivity
public class SettingsActivity extends ActivityBase {


    @OptionsItem(android.R.id.home)
    void onHomeSelected() {
        onBackPressed();
    }

    @Override
    public boolean hasDrawer() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_settings;
    }

    @Override
    public int getContentViewRoot() {
        return R.id.content_main;
    }

    @Override
    public void onCreateUserInterface(TabLayout tabLayout, View mainContentView) {

    }
}
