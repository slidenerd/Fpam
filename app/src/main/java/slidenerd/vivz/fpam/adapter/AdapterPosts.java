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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.ui.transform.CropCircleTransform;
import slidenerd.vivz.fpam.ui.transform.CropTransformation;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.widget.ExpandableTextView;

/**
 * Created by vivz on 29/08/15.
 */
public class AdapterPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * The type of a row that may contain a picture in the post
     */
    private static final int IMAGE = 1;
    /**
     * The type of a row that does not contain a picture in its post
     */
    private static final int NO_IMAGE = 2;
    private Context mContext;
    private Realm mRealm;
    /**
     * The list of posts to be displayed inside this Adapter.
     */
    private RealmResults<Post> mPosts;
    private LayoutInflater mLayoutInflater;
    /**
     * The width of the image found in the post if the post has an image
     */
    private int mPostImageWidth;
    /**
     * The Height of the image found in the post if the post has an image
     */
    private int mPostImageHeight;
    /**
     * The transformation to be applied to the user's profile picture
     */
    private CropCircleTransform mCircleTransform;
    /**
     * The transformation to be applied to the post's image if one is present.
     */
    private CropTransformation mCropTransform;
    /**
     * Keep track of whether an data at a given position is expanded or collapsed, the key is the position whereas the value is boolean indicating whether the data is expanded or collapsed.
     */
    private SparseBooleanArray mState = new SparseBooleanArray();

    /**
     * Instantiates a new Adapter posts. Get the height and width of an image with respect to the screen size of this device.
     * Initialize the transformation to be applied on the user's profile picture.
     * Initialize the transformation to be applied on the post image if there is one.
     *
     * @param context the context
     * @param realm   the realm
     * @param results the results
     */
    public AdapterPosts(Context context, Realm realm, RealmResults<Post> results) {
        mContext = context;
        mRealm = realm;
        mLayoutInflater = LayoutInflater.from(context);

        //Initialize post image width and height
        Point size = DisplayUtils.getPostImageSize(context);
        mPostImageWidth = size.x;
        mPostImageHeight = size.y;

        mCircleTransform = new CropCircleTransform(mContext);
        mCropTransform = new CropTransformation(mContext, mPostImageWidth, mPostImageHeight);
        updateRealmResults(results);
    }

    /**
     * By default, return the type of an item as the one that does not contain an image.
     * If our posts are valid, check if we have a valid image url at the specified position.
     * If we have a valid image url for the post, return the type of the row as the one containing an image
     * If we don't have a valid image url for the post, return the type of the row as the one that doesn't contain images
     *
     * @param position
     * @return The type of an item indicating whether it contains an image in the post or not.
     */
    @Override
    public int getItemViewType(int position) {
        if (mPosts != null) {
            return mPosts.get(position).getPicture() != null ? IMAGE : NO_IMAGE;
        }
        return NO_IMAGE;
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
     * If our post has an image at the specified position, we need to set the width and height of that ImageView.
     * To set the width and height of the ImageView , we get its LayoutParams
     * We maintain an aspect ratio of 16:9 for width and height of the ImageView.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     *                 If the type of our item contains an image in the post, we return the corresponding ViewHolder
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE) {
            view = mLayoutInflater.inflate(R.layout.row_post_image, parent, false);
            RowImageHolder holder = new RowImageHolder(view);
            ViewGroup.LayoutParams params = holder.mPostPicture.getLayoutParams();
            params.width = mPostImageWidth;
            params.height = mPostImageHeight;
            holder.mPostPicture.setLayoutParams(params);
            return holder;
        } else {
            view = mLayoutInflater.inflate(R.layout.row_post_image, parent, false);
            RowNoImageHolder holder = new RowNoImageHolder(view);
            return holder;
        }
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
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mPosts != null && getItemViewType(position) == IMAGE) {
            Post post = mPosts.get(position);
            RowImageHolder holder = (RowImageHolder) viewHolder;
            holder.setUserName(post.getUserName());
            holder.setUpdatedTime(post.getUpdatedTime());
            holder.setMessage(post.getMessage(), mState, position);
            holder.setProfilePicture(post.getUserPicture());
            holder.setPostPicture(post.getPicture());
        } else if (mPosts != null && getItemViewType(position) == NO_IMAGE) {
            Post post = mPosts.get(position);
            RowNoImageHolder holder = (RowNoImageHolder) viewHolder;
            holder.setUserName(post.getUserName());
            holder.setUpdatedTime(post.getUpdatedTime());
            holder.setMessage(post.getMessage(), mState, position);
            holder.setProfilePicture(post.getUserPicture());
        }
    }

    /**
     * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param queryResults the new RealmResults coming from the new query.
     */
    public void updateRealmResults(RealmResults<Post> queryResults) {
        mPosts = queryResults;
        notifyDataSetChanged();
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     * If our list of posts is empty, then we return NO_ID else we return the unique rowId for each post
     */
    @Override
    public long getItemId(int position) {
        return mPosts == null ? RecyclerView.NO_ID : mPosts.get(position).getRowId();
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }


    /**
     * Remove the Post at the specified position from the database.
     * It is assumed here that posts are not null since this method can be called only when the user swipes on a post to delete it.
     *
     * @param position the position
     */
    public void remove(int position) {
        mRealm.beginTransaction();
        mPosts.get(position).removeFromRealm();
        mRealm.commitTransaction();
        notifyItemRemoved(position);
    }

    /**
     * Gets the post at the specified position
     *
     * @param position the position
     * @return the Post at the specified position.
     */
    @Nullable
    public Post getItem(int position) {
        return mPosts.get(position);
    }

    /**
     * The ViewHolder for Posts that don't have an image.
     */
    public class RowNoImageHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mTime;
        private ExpandableTextView mMessage;
        private ImageView mProfilePicture;

        public RowNoImageHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.text_name);
            mTime = (TextView) itemView.findViewById(R.id.text_time);
            mMessage = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
        }

        public void setUserName(String userName) {
            mName.setText(userName);
        }

        /**
         * Sets updated time.
         * If the long parameter is greater than zero, it means we have a valid and available value of time.
         * If we have a valid value of time, we convert it to relative time span and display that.
         * If we don't have a valid value of time, we indicate the same with the help of an appropriate message.
         *
         * @param updatedTimeMillis the updated time millis
         */
        public void setUpdatedTime(long updatedTimeMillis) {
            long now = System.currentTimeMillis();
            String updatedTime = updatedTimeMillis > 0 ? (String) DateUtils.getRelativeTimeSpanString(updatedTimeMillis, now, DateUtils.SECOND_IN_MILLIS) : "NA";
            mTime.setText(updatedTime);
        }

        /**
         * Sets message making it expanded if it was already expanded else collapsed.
         *
         * @param message  the message
         * @param state    the state indicating whether this text is in the expanded or collapsed state.
         * @param position the position under consideration
         */
        public void setMessage(String message, SparseBooleanArray state, int position) {
            mMessage.setText(message, state, position);
        }

        /**
         * Sets profile picture as per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
         *
         * @param uri the uri
         */
        public void setProfilePicture(String uri) {
            if (StringUtils.isNotBlank(uri)) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(mCircleTransform)
                        .into(mProfilePicture);
            } else {
                Glide.clear(mProfilePicture);
                mProfilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }
        }
    }

    /**
     * The ViewHolder for Posts that have an image to be displayed in addition to the user's profile picture.
     */
    public class RowImageHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mTime;
        private ExpandableTextView mMessage;
        private ImageView mPostPicture;
        private ImageView mProfilePicture;

        public RowImageHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.text_name);
            mTime = (TextView) itemView.findViewById(R.id.text_time);
            mMessage = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            mPostPicture = (ImageView) itemView.findViewById(R.id.post_picture);
            mProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);

        }

        public void setUserName(String userName) {
            mName.setText(userName);
        }

        /**
         * Sets updated time.
         * If the long parameter is greater than zero, it means we have a valid and available value of time.
         * If we have a valid value of time, we convert it to relative time span and display that.
         * If we don't have a valid value of time, we indicate the same with the help of an appropriate message.
         *
         * @param updatedTimeMillis the updated time millis
         */
        public void setUpdatedTime(long updatedTimeMillis) {
            long now = System.currentTimeMillis();
            String updatedTime = updatedTimeMillis > 0 ? (String) DateUtils.getRelativeTimeSpanString(updatedTimeMillis, now, DateUtils.SECOND_IN_MILLIS) : "NA";
            mTime.setText(updatedTime);
        }

        /**
         * Sets message making it expanded if it was already expanded else collapsed.
         *
         * @param message  the message
         * @param state    the state indicating whether this text is in the expanded or collapsed state.
         * @param position the position under consideration
         */
        public void setMessage(String message, SparseBooleanArray state, int position) {
            mMessage.setText(message, state, position);
        }

        /**
         * Sets profile picture as per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
         *
         * @param uri the uri
         */
        public void setProfilePicture(String uri) {
            if (StringUtils.isNotBlank(uri)) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(mCircleTransform)
                        .into(mProfilePicture);
            } else {
                Glide.clear(mProfilePicture);
                mProfilePicture.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }
        }

        /**
         * Sets the post picture as per the solution discussed here http://stackoverflow.com/questions/32706246/recyclerview-adapter-and-glide-same-image-every-4-5-rows
         *
         * @param uri the uri
         */
        public void setPostPicture(String uri) {
            if (StringUtils.isNotBlank(uri)) {
                Glide.with(mContext)
                        .load(uri)
                        .asBitmap()
                        .transform(mCropTransform)
                        .into(mPostPicture);
            } else {
                Glide.clear(mPostPicture);
                mPostPicture.setImageDrawable(null);
            }
        }
    }
}