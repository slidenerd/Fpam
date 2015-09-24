package slidenerd.vivz.fpam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.util.PrintUtils;

@EActivity(R.layout.activity_cache)
@OptionsMenu(R.menu.menu_activity_cache_viewer)
public class ActivityCache extends AppCompatActivity {

    @ViewById(R.id.text_cache)
    TextView mTextCache;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void initUI() {
        ArrayList<Post> listPosts = DataStore.loadFeed(mRealm);
        String text = PrintUtils.toString(listPosts);
        mTextCache.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
