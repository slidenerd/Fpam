package slidenerd.vivz.fpam.settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.KeywordsAdapter;
import slidenerd.vivz.fpam.model.realm.Keyword;

@EActivity(R.layout.activity_keywords)
public class ActivityKeywords extends AppCompatActivity {

    @ViewById(R.id.input_keyword)
    EditText mInputKeyword;
    @ViewById(R.id.recycler_keywords)
    RecyclerView mRecyclerKeywords;
    private Realm mRealm;
    private KeywordsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @AfterViews
    void onCreateView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputKeyword != null && mInputKeyword.getText() != null && mInputKeyword.getText().toString() != null && mInputKeyword.getText().toString().trim().length() > 0) {
                    Snackbar.make(view, "Added " + mInputKeyword.getText().toString().trim().toLowerCase(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Keyword keyword = new Keyword();
                    keyword.setKeyword(mInputKeyword.getText().toString().trim().toLowerCase());
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(keyword);
                    mRealm.commitTransaction();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RealmResults<Keyword> mResults = mRealm.where(Keyword.class).findAll();
        mRecyclerKeywords.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new KeywordsAdapter(this, mRealm, mResults);
        mRecyclerKeywords.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
