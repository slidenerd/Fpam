package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.ui.transform.CropTransformation;
import slidenerd.vivz.fpam.util.CopyUtils;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.widget.ExpandableTextView;

/**
 * Refer https://github.com/thorbenprimke/realm-recyclerview/blob/master/library/src/main/java/io/realm/RealmBasedRecyclerViewAdapter.java for implementation details with respect to animation of changes in the data of the adapter.
 * Created by vivz on 29/08/15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemHolder> implements NetworkSwipeHelper.OnSwipeListener {

    private static final List<Long> EMPTY_LIST = new ArrayList<>(0);

    //Item Ids used to track which items were added, removed or modified when a change occurs to our Realm database corresponding to Posts table so that we can animate items accordingly.
    protected List mIds;
    private Context mContext;
    private Realm mRealm;
    private RealmResults<Post> mResults;
    private RealmChangeListener mChangeListener;
    private LayoutInflater mLayoutInflater;
    private DeleteListener mDeleteListener;

    //Width of the image found in the post if there is one
    private int mPostImageWidth;

    //Height of the image found in the post if there is one
    private int mPostImageHeight;

    //Keep track of whether an item at a given position is expanded or collapsed, the key is the position whereas the value is boolean indicating whether the item is expanded or collapsed.
    private SparseBooleanArray mState = new SparseBooleanArray();

    public PostAdapter(Context context, Realm realm, RealmResults<Post> results) {
        mContext = context;
        mRealm = realm;
        mChangeListener = getRealmChangeListener();
        mLayoutInflater = LayoutInflater.from(context);
        //Initialize post image width and height
        Point size = DisplayUtils.getPostImageSize(context);
        mPostImageWidth = size.x;
        mPostImageHeight = size.y;
        updateRealmResults(results);
    }

    private RealmChangeListener getRealmChangeListener() {
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
                    L.m("from adapter notify data set changed");
                    notifyDataSetChanged();
                    mIds = getIdsOfRealmResults();
                }
            }
        };
    }

    public void setDeleteListener(DeleteListener listener) {
        this.mDeleteListener = listener;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_post_2, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Post post = mResults.get(position);
        holder.setUserName(post.getUserName());
        holder.setUpdatedTime(post.getUpdatedTime());
        holder.setMessage(post.getMessage(), mState, position);
        holder.setPostPicture(post.getPicture());
        // Check for an expanded view, collapse if you find one

    }

    /**
     * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param queryResults the new RealmResults coming from the new query.
     */
    public void updateRealmResults(RealmResults<Post> queryResults) {
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

    private List getIdsOfRealmResults() {
        if (mResults == null || mResults.size() == 0) {
            return EMPTY_LIST;
        }
        List ids = new ArrayList(mResults.size());
        for (int i = 0; i < mResults.size(); i++) {
            ids.add(mResults.get(i).getPostId());
        }
        return ids;

    }

    @Override
    public int getItemCount() {
        return mResults == null ? 0 : mResults.size();
    }

    @Override
    public void onSwipe(int position) {
        Post post = mResults.get(position);
        mDeleteListener.triggerDelete(position, CopyUtils.duplicatePost(post));
    }

    public interface DeleteListener {
        void triggerDelete(int position, Post post);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView mTextName;
        private TextView mTextTime;
        private ExpandableTextView mTextMessage;
        private ImageView mPostPicture;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextName = (TextView) itemView.findViewById(R.id.text_name);
            mTextTime = (TextView) itemView.findViewById(R.id.text_time);
            mTextMessage = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mPostPicture = (ImageView) itemView.findViewById(R.id.post_picture);
        }


        public void setUserName(String userName) {
            mTextName.setText(userName);
        }

        public void setUpdatedTime(long updatedTimeMillis) {
            long now = System.currentTimeMillis();
            String updatedTime = updatedTimeMillis > 0 ? (String) DateUtils.getRelativeTimeSpanString(updatedTimeMillis, now, DateUtils.SECOND_IN_MILLIS) : "NA";
            mTextTime.setText(updatedTime);
        }

        public void setMessage(String message, SparseBooleanArray state, int position) {
            mTextMessage.setText(message, state, position);
        }

        public void setPostPicture(String uri) {

            //As per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
            if (uri != null) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(new CropTransformation(mContext, mPostImageWidth, mPostImageHeight))
                        .into(mPostPicture);
            } else {
                Glide.clear(mPostPicture);
                mPostPicture.setImageDrawable(null);
            }
        }
    }
}