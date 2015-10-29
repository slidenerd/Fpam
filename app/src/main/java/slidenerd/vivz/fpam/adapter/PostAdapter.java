package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.ui.transform.CropTransformation;
import slidenerd.vivz.fpam.util.CopyUtils;
import slidenerd.vivz.fpam.util.DisplayUtils;

/**
 * Created by vivz on 29/08/15.
 */
public class PostAdapter extends AbstractRealmAdapter<Post, PostAdapter.ItemHolder> implements SwipeHelper.OnSwipeListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private DeleteListener mListener;


    public PostAdapter(Context context, Realm realm, RealmResults<Post> results) {
        super(context, realm, results);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setDeleteListener(DeleteListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    @Override
    public boolean hasFooter() {
        return false;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_post_2, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        Post post = getItem(position);
        holder.setUserName(post.getUserName());
        holder.setUpdatedTime(post.getUpdatedTime());
        holder.setMessage(post.getMessage());
        String uri = post.getPicture();

        Point size = DisplayUtils.getPostImageSize(mContext);
        if (uri != null) {
            Glide.with(mContext)
                    .load(uri)
                    .asBitmap()
                    .transform(new CropTransformation(mContext, size.x, size.y))
                    .into(holder.mPostPicture);
        } else {
            Glide.clear(holder.itemView);
            holder.mPostPicture.setImageBitmap(null);
        }
    }

    @Override
    public void onViewRecycled(ItemHolder holder) {
        super.onViewRecycled(holder);
        holder.mTextMessage.setText("");
        holder.mPostPicture.setImageBitmap(null);
    }

    @Override
    public void onSwipe(int position) {
        Post post = mResults.get(position);
        mListener.triggerDelete(position, CopyUtils.duplicatePost(post));
    }

    public interface DeleteListener {
        void triggerDelete(int position, Post post);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextName;
        private TextView mTextTime;
        private TextView mTextMessage;
        private ImageView mPostPicture;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextName = (TextView) itemView.findViewById(R.id.text_name);
            mTextTime = (TextView) itemView.findViewById(R.id.text_time);
            mTextMessage = (TextView) itemView.findViewById(R.id.text_message);
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

        public void setMessage(String message) {
            mTextMessage.setText(message);
        }
    }
}