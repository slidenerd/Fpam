package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.realm.Keyword;

/**
 * Created by vivz on 29/08/15.
 */
public class AdapterKeywords extends RecyclerView.Adapter<AdapterKeywords.KeywordHolder> {

    private Realm mRealm;
    /**
     * The list of Keyword objects stored in the database
     */
    private RealmResults<Keyword> mKeywords;
    private LayoutInflater mLayoutInflater;

    /**
     * Instantiates a new Adapter keywords. Calls updateRealmResults to notify changes in the Adapter.
     *
     * @param context  the context
     * @param realm    the realm
     * @param keywords the keywords
     */
    public AdapterKeywords(Context context, Realm realm, RealmResults<Keyword> keywords) {
        mLayoutInflater = LayoutInflater.from(context);
        mRealm = realm;
        updateRealmResults(keywords);
    }

    /**
     * Update realm results. Call notifyDataSetChanged to indicate that the contents of the Adapter has changed.
     *
     * @param queryResults the query results
     */
    public void updateRealmResults(RealmResults<Keyword> queryResults) {
        mKeywords = queryResults;
        notifyDataSetChanged();
    }

    /**
     * Add a new keyword to the database. If it already exists, update the timestamp of the keyword and the list of groups to which it is applicable.
     *
     * @param keyword the keyword string
     */
    public void add(final String keyword) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(new Keyword(keyword, System.currentTimeMillis(), Constants.ALL));
        mRealm.commitTransaction();
        notifyDataSetChanged();
    }

    /**
     * Get the row id associated with the specified position in the list.
     * In this case, our row id is the timestamp associated with when the keyword was added to the database.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return mKeywords.get(position).getTimestamp();
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mKeywords.size();
    }

    /**
     * Gets the keyword from the specified position
     *
     * @param position the position
     * @return the item
     */
    public Keyword getItem(int position) {
        return mKeywords.get(position);
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    public KeywordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_keyword, parent, false);
        return new KeywordHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(KeywordHolder holder, int position) {
        Keyword keyword = mKeywords.get(position);
        holder.setKeyword(keyword.getKeyword());
    }

    /**
     * Remove the keyword from the database at the specified position
     *
     * @param position the position corresponding to which you want to remove the keyword from the database.
     *                 Call notifyItemRemoved after removing the keyword from the given position
     */
    public void remove(int position) {
        mRealm.beginTransaction();
        mKeywords.get(position).removeFromRealm();
        mRealm.commitTransaction();
        notifyItemRemoved(position);
    }

    /**
     * The type Keyword holder.
     */
    public class KeywordHolder extends RecyclerView.ViewHolder {
        private TextView mKeyword;

        public KeywordHolder(View itemView) {
            super(itemView);
            mKeyword = (TextView) itemView.findViewById(R.id.text_keyword);
        }

        public void setKeyword(String keyword) {
            mKeyword.setText(keyword);
        }
    }
}