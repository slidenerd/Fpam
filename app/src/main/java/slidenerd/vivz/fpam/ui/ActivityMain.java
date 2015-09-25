package slidenerd.vivz.fpam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.MainPagerAdapter;
import slidenerd.vivz.fpam.util.FileUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity
@OptionsMenu(R.menu.menu_activity_stats)
public class ActivityMain extends ActivityBase {


    private ViewPager mPager;
    private MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cache) {
            NavUtils.startActivityCache(this);
            return true;
        }
        if (id == R.id.action_settings) {
            NavUtils.startActivitySettings(this);
            return true;
        }
        if (id == R.id.action_test) {

            Intent intent = new Intent(this, TestActivity1.class);
            startActivity(intent);
        }
        if (id == R.id.action_test2) {
            Intent intent = new Intent(this, TestActivity2.class);
            startActivity(intent);
        }
        return false;
    }

    @OptionsItem(R.id.action_export_database)
    void onExportDatabaseSelected() {
        FileUtils.exportDatabase(this);
    }

    @Override
    public int getLayoutForActivity() {
        return R.layout.activity_stats;
    }

    @Override
    public int getRootViewId() {
        return R.id.main_root;
    }

//    /**
//     * This method is called prior to the onCreate inside this Activity. Hence initialize the ViewPager and PagerAdapter here instead of onCreate to avoid null pointer exceptions.
//     */
//    public void addTabs(TabLayout tabLayout) {
//        mPager = (ViewPager) findViewById(R.id.main_root);
//        mPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
//        mPager.setAdapter(mPagerAdapter);
//        tabLayout.setupWithViewPager(mPager);
//    }

    @Override
    @Nullable
    public int getViewPagerId() {
        return R.id.main_root;
    }

    @Override
    @Nullable
    public PagerAdapter getPagerAdapter() {
        return new MainPagerAdapter(this, getSupportFragmentManager());
    }
}