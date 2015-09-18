package slidenerd.vivz.fpam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.realm.RealmAttachment;
import slidenerd.vivz.fpam.model.realm.RealmComment;
import slidenerd.vivz.fpam.model.realm.RealmPost;

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
        RealmResults<RealmPost> realmPosts = mRealm.where(RealmPost.class).findAllSorted("id");
        StringBuilder stringBuilder = new StringBuilder();
        for (RealmPost post : realmPosts) {
            stringBuilder.append("\n" + post.getId() + "\n" + post.getGroupId() + "\n");
            for (RealmComment comment : post.getComments()) {
                stringBuilder.append("\n" + comment + "\n");
            }
            for (RealmAttachment attachment : post.getAttachments()) {
                stringBuilder.append("\n" + attachment + "\n");
            }
        }
        mTextCache.setText(stringBuilder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
