package slidenerd.vivz.fpam.settings;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.PhraseAdapter;
import slidenerd.vivz.fpam.adapter.TouchHelper;
import slidenerd.vivz.fpam.model.phrase.Phrase;

/**
 * This fragment shows notification preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@EFragment(R.layout.frag_spam_phrase)
public class PreFragSpamPhrase extends Fragment {
    @ViewById(R.id.recycler_spam_content)
    RecyclerView mRecyclerSpamPhrases;
    @ViewById(R.id.text_empty)
    TextView mTextEmpty;
    private PhraseAdapter mAdapter;
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity());
    }

    @AfterViews
    public void onViewCreated() {
        RealmResults<Phrase> results = realm.where(Phrase.class).findAllSorted("phrase");
        mAdapter = new PhraseAdapter(getActivity(), realm, results);
        mRecyclerSpamPhrases.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerSpamPhrases.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new TouchHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerSpamPhrases);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}