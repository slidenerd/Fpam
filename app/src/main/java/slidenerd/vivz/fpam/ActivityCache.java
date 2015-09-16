package slidenerd.vivz.fpam;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import slidenerd.vivz.fpam.util.DiskUtils;

@EActivity(R.layout.activity_cache)
@OptionsMenu(R.menu.menu_activity_cache_viewer)
public class ActivityCache extends AppCompatActivity {

    @ViewById(R.id.text_cache)
    TextView mTextCache;

    @AfterViews
    void initUI() {
        String data = DiskUtils.readFromCache(this);
        mTextCache.setText(data);
    }
}
