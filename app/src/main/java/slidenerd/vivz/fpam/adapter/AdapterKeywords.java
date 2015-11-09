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
public class AdapterKeywords extends RecyclerView.Adapter<AdapterKeywords.KeywordHolder> {

    private Realm mRealm;
    private RealmResults<Keyword> mResults;
    private LayoutInflater mLayoutInflater;

    public AdapterKeywords(Context context, Realm realm, RealmResults<Keyword> results) {
        mLayoutInflater = LayoutInflater.from(context);
        mRealm = realm;
        updateRealmResults(results);
    }


    public void updateRealmResults(RealmResults<Keyword> queryResults) {
        mResults = queryResults;
        notifyDataSetChanged();
    }

    public void add(String keywordString) {
        Keyword keyword = new Keyword();
        keyword.setKeyword(keywordString);
        keyword.setTimestamp(System.currentTimeMillis());
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(keyword);
        mRealm.commitTransaction();
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public long getItemId(int position) {
        return mResults.get(position).getTimestamp();
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public Keyword getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public KeywordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_keyword, parent, false);
        return new KeywordHolder(view);
    }

    @Override
    public void onBindViewHolder(KeywordHolder holder, final int position) {
        final Keyword keyword = mResults.get(position);
        holder.setKeyword(keyword.getKeyword());
    }

    public void remove(int position) {
        mRealm.beginTransaction();
        mResults.get(position).removeFromRealm();
        mRealm.commitTransaction();
        notifyItemRemoved(position);
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