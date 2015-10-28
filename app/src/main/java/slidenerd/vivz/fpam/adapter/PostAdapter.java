package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.ui.transform.CropTransformation;
import slidenerd.vivz.fpam.util.CopyUtils;
import slidenerd.vivz.fpam.widget.PostView;

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
        View view = mLayoutInflater.inflate(R.layout.row_post, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        CustomViewTarget target = new CustomViewTarget(holder.mPostView, position);
        Post post = getItem(position);
        holder.setUserName(post.getUserName());
        holder.setUpdatedTime(post.getUpdatedTime());
        holder.setMessage(post.getMessage());
        String uri = post.getPicture();

        if (uri != null) {
            Glide.with(mContext)
                    .load(uri)
                    .asBitmap()
                    .transform(new CropTransformation(mContext, holder.getPostPictureWidth(), holder.getPostPictureHeight()))
                    .into(target);
        } else {
            Glide.clear(holder.mPostView);
            holder.mPostView.setPostPicture(null);
        }
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

        private PostView mPostView;

        public ItemHolder(View itemView) {
            super(itemView);
            mPostView = (PostView) itemView.findViewById(R.id.post_view);
        }


        public void setUserName(String userName) {
            mPostView.setUserName(userName);
        }

        public void setUpdatedTime(long updatedTimeMillis) {
            long now = System.currentTimeMillis();
            String updatedTime = updatedTimeMillis > 0 ? (String) DateUtils.getRelativeTimeSpanString(updatedTimeMillis, now, DateUtils.SECOND_IN_MILLIS) : "NA";
            mPostView.setUpdatedTime(updatedTime);
        }

        public void setMessage(String message) {
            mPostView.setMessage(message);
        }

        public int getPostPictureWidth() {
            return mPostView.getPostPictureWidth();
        }

        public int getPostPictureHeight() {
            return mPostView.getPostPictureHeight();
        }
    }

    public class CustomViewTarget extends ViewTarget<PostView, Bitmap> {

        int position;

        public CustomViewTarget(PostView view, int position) {
            super(view);
            this.position = position;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            view.setPostPicture(resource);
        }
    }
}