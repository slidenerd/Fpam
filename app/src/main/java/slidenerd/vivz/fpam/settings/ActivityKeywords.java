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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.AdapterKeywordGroups;
import slidenerd.vivz.fpam.adapter.AdapterKeywords;
import slidenerd.vivz.fpam.adapter.OnItemClickListener;
import slidenerd.vivz.fpam.adapter.RecyclerViewHelperImpl;
import slidenerd.vivz.fpam.adapter.SwipeToDismissTouchListener;
import slidenerd.vivz.fpam.adapter.SwipeableItemClickListener;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.pojo.KeywordGroup;
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
    RecyclerView mRecycler;
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
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AdapterKeywords(this, mRealm, keywords);
        mAdapter.setHasStableIds(true);

        mRecycler.setAdapter(mAdapter);
        final SwipeToDismissTouchListener<RecyclerViewHelperImpl> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewHelperImpl(mRecycler),
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

        mRecycler.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecycler.addOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecycler.addOnItemTouchListener(new SwipeableItemClickListener(this,
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

        //get the keyword at the specified position.
        final Keyword keyword = mAdapter.getItem(position);

        //find all the groups in fpam
        final RealmResults<Group> allGroups = mRealm.where(Group.class).findAllSortedAsync(GROUP_NAME);

        //add a ChangeListener to be triggered when all the groups have finished loading asynchronously
        allGroups.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {

                //Create an empty list of KeywordGroup objects, each object will mirror the group id and group name from the Groups list
                ArrayList<KeywordGroup> keywordGroups = new ArrayList<KeywordGroup>();
                for (Group group : allGroups) {
                    keywordGroups.add(new KeywordGroup(group.getGroupId(), group.getGroupName()));
                }

                //Initialize our Adapter with the list of KeywordGroup objects
                final AdapterKeywordGroups adapter = new AdapterKeywordGroups(ActivityKeywords.this, keywordGroups);

                //Get a CSV list of groups for our current Keyword
                String groups = keyword.getGroups();

                //If a keyword applies to all groups, its does not store all the group ids, instead if stores a simple String "ALL" indicating that this keyword is applicable on all group ids to conserve space
                if (StringUtils.equals(groups, Constants.ALL)) {
                    adapter.selectAll();
                }

                //if a keyword applies to a few groups, it stores their group ids in the form of a CSV list. In this case, fetch the string, split it on the basis of comma and get a list of all group ids
                else {
                    List<String> selected = Arrays.asList(StringUtils.split(groups, ','));
                    adapter.select(selected);
                }
                new MaterialDialog.Builder(ActivityKeywords.this)
                        .title(getString(R.string.title_dialog_applicable_groups, keyword.getKeyword()))
                        .adapter(adapter, null)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                mRealm.beginTransaction();

                                //if all the data are selected by the user, then instead of storing a CSV list of all the group ids, simply store a Stirng "ALL" indicating that this keyword is applicable on all groups
                                if (adapter.isAllSelected()) {
                                    keyword.setGroups(Constants.ALL);
                                } else {

                                    //If not all groups are selected by the user, get a list of group ids that are selected.
                                    ArrayList<String> selected = adapter.getSelected();

                                    //Convert the list to a CSV and store the CSV
                                    String groups = StringUtils.join(selected.toArray(), ',');
                                    keyword.setGroups(groups);
                                }
                                mRealm.commitTransaction();
                            }
                        })
                        .build()
                        .show();

                //Remove this for not receiving any further notifications.
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


