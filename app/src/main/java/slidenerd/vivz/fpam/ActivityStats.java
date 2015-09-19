package slidenerd.vivz.fpam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import slidenerd.vivz.fpam.util.FileUtils;
import slidenerd.vivz.fpam.util.NavUtils;

@EActivity
@OptionsMenu(R.menu.menu_activity_stats)
public class ActivityStats extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mRefreshFeed;
    private RecyclerView mRecyclerFeed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshFeed = (SwipeRefreshLayout) findViewById(R.id.refresh_feed);
        mRecyclerFeed = (RecyclerView) findViewById(R.id.recycler_feed);
        mRecyclerFeed.setLayoutManager(new LinearLayoutManager(this));
        mRefreshFeed.setOnRefreshListener(this);
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

    @Override
    public void onRefresh() {
        mRefreshFeed.setRefreshing(false);
    }
}