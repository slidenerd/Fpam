package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.realm.Keyword;

/**
 * Created by vivz on 29/08/15.
 */
public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordHolder> implements KeywordSwipehelper.OnSwipeListener {

    private static final List<Long> EMPTY_LIST = new ArrayList<>(0);
    private Realm mRealm;
    private RealmResults<Keyword> mResults;
    private LayoutInflater mLayoutInflater;
    private List mIds;
    private RealmChangeListener mChangeListener;
    private IconClickListener mClickListener;

    public KeywordAdapter(Context context, Realm realm, RealmResults<Keyword> results) {
        mLayoutInflater = LayoutInflater.from(context);
        mRealm = realm;
        mResults = results;
        mChangeListener = getRealmChangeListener();
        updateRealmResults(results);
    }

    public void setIconClickListener(IconClickListener listener) {
        mClickListener = listener;
    }

    public RealmChangeListener getRealmChangeListener() {
        return new RealmChangeListener() {
            @Override
            public void onChange() {
                if (mIds != null && !mIds.isEmpty()) {
                    List newIds = getIdsOfRealmResults();
                    // If the list is now empty, just notify the recyclerView of the change.
                    if (newIds.isEmpty()) {
                        mIds = newIds;
                        notifyDataSetChanged();
                        return;
                    }
                    Patch patch = DiffUtils.diff(mIds, newIds);
                    List<Delta> deltas = patch.getDeltas();
                    mIds = newIds;
                    if (deltas.isEmpty()) {
                        // Nothing has changed - most likely because the notification was for
                        // a different object/table
                    } else if (deltas.size() > 1) {
                        notifyDataSetChanged();
                    } else {
                        Delta delta = deltas.get(0);
                        if (delta.getType() == Delta.TYPE.INSERT) {
                            if (delta.getRevised().size() == 1) {
                                notifyItemInserted(delta.getRevised().getPosition());
                            } else {
                                notifyDataSetChanged();
                            }
                        } else if (delta.getType() == Delta.TYPE.DELETE) {
                            if (delta.getOriginal().size() == 1) {
                                notifyItemRemoved(delta.getOriginal().getPosition());
                            } else {
                                // Note: The position zero check is to hack around a indexOutOfBound
                                // exception that happens when the zero position is animated out.
                                if (delta.getOriginal().getPosition() == 0) {
                                    notifyDataSetChanged();
                                    return;
                                } else {
                                    notifyItemRangeRemoved(
                                            delta.getOriginal().getPosition(),
                                            delta.getOriginal().size());
                                }
                            }

                            if (delta.getOriginal().getPosition() - 1 > 0) {
                                notifyItemRangeChanged(
                                        0,
                                        delta.getOriginal().getPosition() - 1);
                            }
                            if (delta.getOriginal().getPosition() > 0 &&
                                    newIds.size() > 0) {
                                notifyItemRangeChanged(
                                        delta.getOriginal().getPosition(),
                                        newIds.size() - 1);
                            }
                        } else {
                            notifyDataSetChanged();
                        }
                    }
                } else {
                    notifyDataSetChanged();
                    mIds = getIdsOfRealmResults();
                }
            }
        };
    }

    private List getIdsOfRealmResults() {
        if (mResults == null || mResults.size() == 0) {
            return EMPTY_LIST;
        }
        List ids = new ArrayList(mResults.size());
        for (int i = 0; i < mResults.size(); i++) {
            ids.add(mResults.get(i).getKeyword());
        }
        return ids;

    }

    public void updateRealmResults(RealmResults<Keyword> queryResults) {
        if (mChangeListener != null) {
            if (this.mResults != null) {
                mResults.removeChangeListener(mChangeListener);
            }
        }
        this.mResults = queryResults;
        if (mResults != null && queryResults != null) {
            mResults.addChangeListener(mChangeListener);
        }
        mIds = getIdsOfRealmResults();
        notifyDataSetChanged();
    }

    public void add(String keywordString) {
        Keyword keyword = new Keyword();
        keyword.setKeyword(keywordString);
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(keyword);
        mRealm.commitTransaction();
        notifyItemRangeChanged(0, getItemCount());
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
    public void onBindViewHolder(KeywordHolder holder, final int position) {
        final Keyword keyword = mResults.get(position);
        holder.setKeyword(keyword.getKeyword());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClickIcon(position, keyword);
                }
            }
        });
    }

    @Override
    public void onSwipe(int position) {
        mRealm.beginTransaction();
        mResults.get(position).removeFromRealm();
        mRealm.commitTransaction();
        notifyItemRemoved(position);
    }

    public interface IconClickListener {
        void onClickIcon(int position, Keyword keyword);
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