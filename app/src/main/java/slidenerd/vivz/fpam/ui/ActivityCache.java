package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.PrintUtils;

@EActivity(R.layout.activity_cache)
@OptionsMenu(R.menu.menu_activity_cache_viewer)
public class ActivityCache extends AppCompatActivity {

    @ViewById(R.id.spinner_database)
    Spinner mSpinnerDatabase;
    @ViewById(R.id.spinner_group)
    Spinner mSpinnerGroup;
    @ViewById(R.id.text_cache)
    TextView mTextCache;
    @InstanceState
    int groupSelected = -1;
    private Realm mRealm;
    private RealmChangeListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void initUI() {
        String[] tables = new String[]{"Admin", "Group", "Post", "Spammer"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tables);
        ArrayList<Group> groups = DataStore.copyLoadGroups(mRealm);
        ArrayList<String> groupIdNames = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupIdNames.add(group.getName() + ":" + group.getId());
        }
        mSpinnerDatabase.setAdapter(arrayAdapter);
        mSpinnerGroup.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupIdNames));

    }

    @ItemSelect(R.id.spinner_group)
    public void groupItemSelected(boolean selected, String item) {
        groupSelected = mSpinnerGroup.getSelectedItemPosition();
        String groupId = item.substring(item.indexOf(":") + 1, item.length());
        Group temp = new Group(groupId, "", "", 0, 0, false);
        final RealmResults<Post> results = mRealm.where(Post.class).beginsWith("postId", temp.getId()).findAllSortedAsync("updatedTime", false);
        mListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (results.isLoaded()) {
                    StringBuffer text = new StringBuffer();
                    for (Post post : results) {
                        text.append(PrintUtils.toString(post));
                    }
                    mTextCache.setText(text);
                }
            }
        };
        results.addChangeListener(mListener);
    }

    @OptionsItem(R.id.action_clear_group)
    public void onClearAllPostsInGroupSelected() {
        if (groupSelected != -1) {
            String groupIdName = (String) mSpinnerGroup.getAdapter().getItem(groupSelected);
            String groupId = groupIdName.substring(groupIdName.indexOf(":") + 1, groupIdName.length());
            mRealm.beginTransaction();
            mRealm.where(Post.class).beginsWith("postId", groupId).findAllSorted("updatedTime", false).clear();
            Group group = mRealm.where(Group.class).equalTo("id", groupId).findFirst();
            group.setTimestamp(0);
            mRealm.commitTransaction();
            groupItemSelected(true, groupIdName);

        }
    }

    @ItemSelect(R.id.spinner_database)
    public void databaseItemSelected(boolean selected, String item) {
        StringBuffer text = new StringBuffer();
        if (item.equals("Admin")) {
            Admin admin = mRealm.where(Admin.class).findFirst();
            text.append(PrintUtils.toString(admin));
            mSpinnerGroup.setVisibility(View.GONE);
        } else if (item.equals("Group")) {
            RealmResults<Group> results = mRealm.where(Group.class).findAll();
            for (Group group : results) {
                text.append(PrintUtils.toString(group));
            }
            mSpinnerGroup.setVisibility(View.GONE);

        } else if (item.equals("Post")) {
            mSpinnerGroup.setVisibility(View.VISIBLE);
        } else if (item.equals("Spammer")) {
            RealmResults<Spammer> results = mRealm.where(Spammer.class).findAllSorted("userName");
            for (Spammer feed : results) {
                text.append(PrintUtils.toString(feed));
            }
            mSpinnerGroup.setVisibility(View.GONE);
        }
        mTextCache.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}

