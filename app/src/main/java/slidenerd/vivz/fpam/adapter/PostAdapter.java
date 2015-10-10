package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.util.CopyUtils;
import slidenerd.vivz.fpam.util.DisplayUtils;

/**
 * Created by vivz on 29/08/15.
 */
public class PostAdapter extends AbstractRealmAdapter<Post, PostAdapter.ItemHolder> implements SwipeHelper.OnSwipeListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private DeleteListener mListener;

    private int screenWidth;

    private int imageViewHeight;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public PostAdapter(Context context, Realm realm, RealmResults<Post> results) {
        super(context, realm, results);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        screenWidth = DisplayUtils.getWidthPixels(mContext);
        imageViewHeight = (int) (screenWidth * 9.0 / 16.0);

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
        View view = mLayoutInflater.inflate(R.layout.row_post, parent, false);
        ItemHolder holder = new ItemHolder(view);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, imageViewHeight);
        layoutParams.addRule(RelativeLayout.BELOW, holder.mTextMessage.getId());
        new RelativeLayout.LayoutParams(screenWidth, imageViewHeight);
        holder.mPicture.setLayoutParams(layoutParams);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Post post = getItem(position);
        holder.setUserName(post.getUserName());
        holder.setUpdatedTime(post.getUpdatedTime());
        holder.setMessage(post.getMessage());
        holder.setPicture(post.getPicture());
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

        private TextView mTextUserName;
        private TextView mTextUpdatedTime;
        private TextView mTextMessage;
        private ImageView mPicture;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextUserName = (TextView) itemView.findViewById(R.id.user_name);
            mTextUpdatedTime = (TextView) itemView.findViewById(R.id.updated_time);
            mTextMessage = (TextView) itemView.findViewById(R.id.message);
            mPicture = (ImageView) itemView.findViewById(R.id.picture);

        }

        public void setUserName(String userName) {
            mTextUserName.setText(userName);
        }

        public void setUpdatedTime(long updatedTimeMillis) {
            long now = System.currentTimeMillis();
            String updatedTime = updatedTimeMillis > 0 ? (String) DateUtils.getRelativeTimeSpanString(updatedTimeMillis, now, DateUtils.SECOND_IN_MILLIS) : "NA";
            mTextUpdatedTime.setText(updatedTime);
        }

        public void setMessage(String message) {
            mTextMessage.setText(message);
        }

        public void setPicture(String picture) {
            if (picture != null && !picture.toString().trim().isEmpty()) {
                mPicture.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(picture).into(mPicture);
            } else {
                mPicture.setVisibility(View.GONE);
            }
        }
    }
}