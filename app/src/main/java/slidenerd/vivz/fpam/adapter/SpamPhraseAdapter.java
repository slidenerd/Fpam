package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.SpamPhrase;

/**
 * Created by vivz on 29/08/15.
 */
public class SpamPhraseAdapter extends AbstractRealmAdapter<SpamPhrase, RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public SpamPhraseAdapter(Context context, Realm realm) {
        super(context, realm);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public static boolean isValidPhrase(EditText inputPhrase) {
        return inputPhrase != null
                && inputPhrase.getText() != null
                && inputPhrase.getText().toString().trim() != null
                && !inputPhrase.getText().toString().trim().isEmpty();
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public boolean hasFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ItemType.HEADER.ordinal()) {
            View view = mLayoutInflater.inflate(R.layout.header_spam_phrase, parent, false);
            viewHolder = new HeaderHolder(view);
        } else {
            View view = mLayoutInflater.inflate(R.layout.row_spam_phrase, parent, false);
            viewHolder = new ItemHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            SpamPhrase spamPhrase = mRealmResults.get(position - getHeaderCount());
            itemHolder.mTextSpamPhrase.setText(spamPhrase.getSpamPhrase());
        }
    }

    @Override
    public RealmResults<SpamPhrase> getResults(Realm realm) {
        return realm.where(SpamPhrase.class).findAllSorted("spamPhrase");
    }

    public class HeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private EditText mInputPhrase;
        private ImageButton mBtnAdd;

        public HeaderHolder(View itemView) {
            super(itemView);
            mInputPhrase = (EditText) itemView.findViewById(R.id.input_spam_phrase);
            mBtnAdd = (ImageButton) itemView.findViewById(R.id.btn_add);
            mBtnAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isValidPhrase(mInputPhrase)) {
                SpamPhrase phrase = new SpamPhrase(mInputPhrase.getText().toString().trim().toLowerCase(), System.currentTimeMillis());
                add(phrase, true);
                mInputPhrase.setText("");
            }
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextSpamPhrase;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextSpamPhrase = (TextView) itemView.findViewById(R.id.text_spam_phrase);
        }
    }
}
