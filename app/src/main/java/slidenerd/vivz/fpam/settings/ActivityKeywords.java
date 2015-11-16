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
import slidenerd.vivz.fpam.adapter.AdapterKeywordGroups;
import slidenerd.vivz.fpam.adapter.AdapterKeywords;
import slidenerd.vivz.fpam.adapter.OnItemClickListener;
import slidenerd.vivz.fpam.adapter.RecyclerViewHelperImpl;
import slidenerd.vivz.fpam.adapter.SwipeToDismissTouchListener;
import slidenerd.vivz.fpam.adapter.SwipeableItemClickListener;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;

import static slidenerd.vivz.fpam.extras.Constants.GROUP_NAME;
import static slidenerd.vivz.fpam.extras.Constants.KEYWORD;

@EActivity(R.layout.activity_keywords)
public class ActivityKeywords extends AppCompatActivity {

    @ViewById(R.id.app_bar)
    Toolbar mToolbar;
    @ViewById(R.id.input_keyword)
    EditText mInputKeyword;
    @ViewById(R.id.recycler_keywords)
    RecyclerView mRecyclerKeywords;
    private Realm mRealm;
    private AdapterKeywords mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void onCreateView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final RealmResults<Keyword> keywords = mRealm.where(Keyword.class).findAllSortedAsync(KEYWORD);
        keywords.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerKeywords.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AdapterKeywords(this, mRealm, keywords);
        mAdapter.setHasStableIds(true);

        mRecyclerKeywords.setAdapter(mAdapter);
        final SwipeToDismissTouchListener<RecyclerViewHelperImpl> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewHelperImpl(mRecyclerKeywords),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewHelperImpl>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewHelperImpl view, int position) {
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
                            showKeywordGroups(position);
                        }
                    }
                }));
    }

    public void showKeywordGroups(int position) {
        final Keyword keyword = mAdapter.getItem(position);
        final RealmResults<Group> allGroups = mRealm.where(Group.class).findAllSortedAsync(GROUP_NAME);
        allGroups.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                final AdapterKeywordGroups adapter = new AdapterKeywordGroups(ActivityKeywords.this, allGroups, true);
                RealmList<Group> selectedGroups = keyword.getGroups();
                if (selectedGroups.isEmpty()) {
                    adapter.selectAll();
                } else {
                    adapter.select(selectedGroups);
                }
                new MaterialDialog.Builder(ActivityKeywords.this)
                        .title(getString(R.string.title_dialog_applicable_groups, keyword.getKeyword()))
                        .adapter(adapter, null)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                RealmList<Group> groups = adapter.getSelected();
                                mRealm.beginTransaction();
                                keyword.setGroups(groups);
                                mRealm.commitTransaction();
                            }
                        })
                        .build()
                        .show();
                allGroups.removeChangeListeners();
            }

        });


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
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}


