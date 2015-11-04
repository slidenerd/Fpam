package slidenerd.vivz.fpam.settings;

//TODO use a baseadapter for material dialog display of groups with a checkbox

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.KeywordAdapter;
import slidenerd.vivz.fpam.adapter.KeywordGroupsAdapter;
import slidenerd.vivz.fpam.adapter.KeywordSwipehelper;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;

@EActivity(R.layout.activity_keywords)
public class ActivityKeywords extends AppCompatActivity implements View.OnClickListener, KeywordAdapter.IconClickListener, RealmChangeListener {

    @ViewById(R.id.input_keyword)
    EditText mInputKeyword;
    @ViewById(R.id.recycler_keywords)
    RecyclerView mRecyclerKeywords;
    private Realm mRealm;
    private RealmResults<Group> mGroups;
    private Keyword mSelectedKeyword;
    private KeywordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void onCreateView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RealmResults<Keyword> results = mRealm.where(Keyword.class).findAllSortedAsync("keyword");
        mRecyclerKeywords.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new KeywordAdapter(this, mRealm, results);
        mAdapter.setIconClickListener(this);
        ItemTouchHelper.Callback callback = new KeywordSwipehelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        mRecyclerKeywords.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerKeywords);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(View view) {
        if (mInputKeyword != null
                && mInputKeyword.getText() != null
                && mInputKeyword.getText().toString() != null
                && mInputKeyword.getText().toString().trim().length() > 0) {
            Snackbar.make(view, "Added " + mInputKeyword.getText().toString().trim().toLowerCase(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mAdapter.add(mInputKeyword.getText().toString().trim().toLowerCase());
            mInputKeyword.getEditableText().clear();
        }
    }

    @Override
    public void onClickIcon(int position, final Keyword keyword) {

        mSelectedKeyword = keyword;
        mGroups = mRealm.where(Group.class).findAllSortedAsync("name");
        mGroups.addChangeListener(this);
    }

    @Override
    public void onChange() {
        final KeywordGroupsAdapter adapter = new KeywordGroupsAdapter(this, mGroups, true);
        RealmList<Group> selectedGroups = mSelectedKeyword.getGroups();
        if (selectedGroups.isEmpty()) {
            adapter.selectAll();
        } else {
            adapter.select(selectedGroups);
        }
        new MaterialDialog.Builder(ActivityKeywords.this)
                .title("Groups where \"" + mSelectedKeyword.getKeyword() + "\" is applicable")
                .adapter(adapter, null)
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (!adapter.allSelected()) {
                            RealmList<Group> groups = adapter.getSelected();
                            mRealm.beginTransaction();
                            mSelectedKeyword.setGroups(groups);
                            mRealm.commitTransaction();
                        }
                    }
                })
                .build()
                .show();
    }
}

