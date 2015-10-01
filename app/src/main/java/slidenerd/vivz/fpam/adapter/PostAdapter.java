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
import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 29/08/15.
 */
public class PostAdapter extends AbstractRealmAdapter<Post, PostAdapter.ItemHolder> implements OnSwipeListener {

    private LayoutInflater mLayoutInflater;

    private OnDeleteListener listener;

    public PostAdapter(Context context, Realm realm, RealmResults<Post> results) {
        super(context, realm, results);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.listener = listener;
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
    public void onBindViewHolder(ItemHolder holder, int position) {
        Post post = getItem(position);
        holder.setUserName(post.getUserName());
        holder.setUpdatedTime(post.getUpdatedTime());
        holder.setMessage(post.getMessage());
    }

    @Override
    public void onSwipe(int position) {
        Post post = mResults.get(position);
        listener.beforeDelete(position, post);
    }

    public interface OnDeleteListener {
        public void beforeDelete(int position, Post post);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextUserName;
        private TextView mTextUpdatedTime;
        private TextView mTextMessage;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextUserName = (TextView) itemView.findViewById(R.id.user_name);
            mTextUpdatedTime = (TextView) itemView.findViewById(R.id.updated_time);
            mTextMessage = (TextView) itemView.findViewById(R.id.message);
        }

        public void setUserName(String userName) {
            mTextUserName.setText(userName);
        }

        public void setUpdatedTime(String updatedTime) {
            mTextUpdatedTime.setText(updatedTime);
        }

        public void setMessage(String message) {
            mTextMessage.setText(message);
        }
    }
}