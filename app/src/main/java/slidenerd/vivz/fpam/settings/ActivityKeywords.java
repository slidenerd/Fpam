package slidenerd.vivz.fpam.settings;

//TODO use a baseadapter for material dialog display of groups with a checkbox

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.KeywordAdapter;
import slidenerd.vivz.fpam.adapter.KeywordGroupsAdapter;
import slidenerd.vivz.fpam.adapter.OnItemClickListener;
import slidenerd.vivz.fpam.adapter.RecyclerViewAdapter;
import slidenerd.vivz.fpam.adapter.SwipeToDismissTouchListener;
import slidenerd.vivz.fpam.adapter.SwipeableItemClickListener;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;

@EActivity(R.layout.activity_keywords)
public class ActivityKeywords extends AppCompatActivity implements RealmChangeListener {

    @ViewById(R.id.app_bar)
    Toolbar mToolbar;
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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RealmResults<Keyword> results = mRealm.where(Keyword.class).findAllSortedAsync("keyword");
        mRecyclerKeywords.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new KeywordAdapter(this, mRealm, results);
        mAdapter.setHasStableIds(true);
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerKeywords.setAdapter(mAdapter);
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(mRecyclerKeywords),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                mAdapter.remove(position);
                            }
                        });

        mRecyclerKeywords.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerKeywords.addOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecyclerKeywords.addOnItemTouchListener(new SwipeableItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            mSelectedKeyword = mAdapter.getItem(position);
                            mGroups = mRealm.where(Group.class).findAllSortedAsync("groupName");
                            mGroups.addChangeListener(ActivityKeywords.this);
                        }
                    }
                }));
    }


    @Click(R.id.fab)
    public void onClickAdd() {
        if (mInputKeyword != null
                && mInputKeyword.getText() != null
                && mInputKeyword.getText().toString() != null
                && mInputKeyword.getText().toString().trim().length() > 0) {
            Snackbar.make(mInputKeyword, "Added " + mInputKeyword.getText().toString().trim().toLowerCase(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mAdapter.add(mInputKeyword.getText().toString().trim().toLowerCase());
            mInputKeyword.getEditableText().clear();
        }
    }

    @EditorAction(R.id.input_keyword)
    void onActionDone() {
        onClickAdd();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}

