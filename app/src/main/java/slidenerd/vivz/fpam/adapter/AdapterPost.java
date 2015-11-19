package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.ui.transform.CropCircleTransform;
import slidenerd.vivz.fpam.ui.transform.CropTransformation;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.widget.ExpandableTextView;

/**
 * Refer https://github.com/thorbenprimke/realm-recyclerview/blob/master/library/src/main/java/io/realm/RealmBasedRecyclerViewAdapter.java for implementation details with respect to animation of changes in the data of the adapter.
 * Created by vivz on 29/08/15.
 */
public class AdapterPost extends RecyclerView.Adapter<AdapterPost.ItemHolder> {

    private Context mContext;
    private Realm mRealm;
    private RealmResults<Post> mResults;
    private LayoutInflater mLayoutInflater;

    //Width of the image found in the post if there is one
    private int mPostImageWidth;

    //Height of the image found in the post if there is one
    private int mPostImageHeight;

    private CropCircleTransform mUserTransform;

    private CropTransformation mPostTransform;

    //Keep track of whether an item at a given position is expanded or collapsed, the key is the position whereas the value is boolean indicating whether the item is expanded or collapsed.
    private SparseBooleanArray mState = new SparseBooleanArray();

    public AdapterPost(Context context, Realm realm, RealmResults<Post> results) {
        mContext = context;
        mRealm = realm;
        mLayoutInflater = LayoutInflater.from(context);

        //Initialize post image width and height
        Point size = DisplayUtils.getPostImageSize(context);
        mPostImageWidth = size.x;
        mPostImageHeight = size.y;

        mUserTransform = new CropCircleTransform(mContext);
        mPostTransform = new CropTransformation(mContext, mPostImageWidth, mPostImageHeight);
        updateRealmResults(results);
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_post, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (mResults != null) {
            Post post = mResults.get(position);
            holder.setUserName(post.getUserName());
            holder.setUpdatedTime(post.getUpdatedTime());
            holder.setMessage(post.getMessage(), mState, position);
            holder.setPostPicture(post.getPicture());
            holder.setProfilePicture(post.getUserPicture());
        }
        // Check for an expanded view, collapse if you find one

    }

    /**
     * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param queryResults the new RealmResults coming from the new query.
     */
    public void updateRealmResults(RealmResults<Post> queryResults) {
         mResults = queryResults;
    }

    @Override
    public long getItemId(int position) {
        return mResults == null ? RecyclerView.NO_ID : mResults.get(position).getRowId();
    }

    @Override
    public int getItemCount() {
        return mResults == null ? 0 : mResults.size();
    }

    public void remove(int position) {
        mRealm.beginTransaction();
        mResults.get(position).removeFromRealm();
        mRealm.commitTransaction();
        notifyItemRemoved(position);
    }

    @Nullable
    public Post getItem(int position) {
        return mResults.get(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView mTextName;
        private TextView mTextTime;
        private ExpandableTextView mTextMessage;
        private ImageView mPostPicture;
        private ImageView mProfilePicture;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextName = (TextView) itemView.findViewById(R.id.text_name);
            mTextTime = (TextView) itemView.findViewById(R.id.text_time);
            mTextMessage = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mPostPicture = (ImageView) itemView.findViewById(R.id.post_picture);
            mProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
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

        public void setProfilePicture(String uri) {

            //As per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
            if (StringUtils.isNotBlank(uri)) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(mUserTransform)
                        .into(mProfilePicture);
            } else {
                Glide.clear(mProfilePicture);
                mProfilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }
        }

        public void setPostPicture(String uri) {

            //As per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
            if (StringUtils.isNotBlank(uri)) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(mPostTransform)
                        .into(mPostPicture);
            } else {
                Glide.clear(mPostPicture);
                mPostPicture.setImageDrawable(null);
            }
        }
    }
}