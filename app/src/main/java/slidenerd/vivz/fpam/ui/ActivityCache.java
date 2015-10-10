package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.realm.GroupMeta;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.PrintUtils;

@EActivity(R.layout.activity_cache)
@OptionsMenu(R.menu.menu_activity_cache_viewer)
public class ActivityCache extends AppCompatActivity {

    @ViewById(R.id.spinner_database)
    Spinner mSpinner;
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
        String[] tables = new String[]{"Admin", "Group", "Group Meta", "Post", "Spammer"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tables);
        mSpinner.setAdapter(arrayAdapter);


    }

    @ItemSelect(R.id.spinner_database)
    public void itemSelected(boolean selected, String item) {
        StringBuffer text = new StringBuffer();
        if (item.equals("Admin")) {
            Admin admin = mRealm.where(Admin.class).findFirst();
            text.append(PrintUtils.toString(admin));

        } else if (item.equals("Group")) {
            RealmResults<Group> results = mRealm.where(Group.class).findAll();
            for (Group group : results) {
                text.append(PrintUtils.toString(group));
            }

        } else if (item.equals("Group Meta")) {
            RealmResults<GroupMeta> results = mRealm.where(GroupMeta.class).findAll();
            for (GroupMeta groupMeta : results) {
                text.append(PrintUtils.toString(groupMeta));
            }
        } else if (item.equals("Post")) {
            RealmResults<Post> results = mRealm.where(Post.class).findAll();
            for (Post post : results) {
                text.append(PrintUtils.toString(post));
            }
        } else {
            RealmResults<Spammer> results = mRealm.where(Spammer.class).findAll();
            for (Spammer feed : results) {
                text.append(PrintUtils.toString(feed));
            }
        }
        mTextCache.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}

