package slidenerd.vivz.fpam.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.adapter.SpamPhraseAdapter;
import slidenerd.vivz.fpam.adapter.SwipeDragHelper;

/**
 * This fragment shows notification preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PreFragSpamPhrase extends Fragment {
    private SpamPhraseAdapter mAdapter;
    private RecyclerView mRecyclerSpamPhrases;
    private Context mContext;
    private Realm realm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_spam_phrase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView emptyView = (TextView) view.findViewById(R.id.text_empty);
        mAdapter = new SpamPhraseAdapter(getActivity(), realm);
        mRecyclerSpamPhrases = (RecyclerView) view.findViewById(R.id.recycler_spam_content);
        mRecyclerSpamPhrases.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerSpamPhrases.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SwipeDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerSpamPhrases);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}