package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.realm.Keyword;

/**
 * Created by vivz on 29/08/15.
 */
public class KeywordsAdapter extends RecyclerView.Adapter<KeywordsAdapter.KeywordHolder> {

    private Realm mRealm;
    private RealmResults<Keyword> mResults;
    private LayoutInflater mLayoutInflater;

    public KeywordsAdapter(Context context, Realm realm, RealmResults<Keyword> results) {
        mRealm = realm;
        mResults = results;
        mLayoutInflater = LayoutInflater.from(context);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    public KeywordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_keyword, parent, false);
        return new KeywordHolder(view);
    }

    @Override
    public void onBindViewHolder(KeywordHolder holder, int position) {
        Keyword keyword = mResults.get(position);
        holder.setKeyword(keyword.getKeyword());
    }

    public class KeywordHolder extends RecyclerView.ViewHolder {

        private TextView mTextKeyword;

        public KeywordHolder(View itemView) {
            super(itemView);
            mTextKeyword = (TextView) itemView.findViewById(R.id.text_keyword);
        }

        public void setKeyword(String keyword) {
            mTextKeyword.setText(keyword);
        }
    }
}