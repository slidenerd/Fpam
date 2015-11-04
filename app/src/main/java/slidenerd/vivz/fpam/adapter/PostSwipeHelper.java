package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_POST;

public class PostSwipeHelper extends ItemTouchHelper.Callback {

    private final PostAdapter mAdapter;
    private Context mContext;

    public PostSwipeHelper(Context context, PostAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    /**
     * @return false if you dont want to enable drag else return true
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * @return true of you want to enable swipe in your RecyclerView else return false
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //We want to let the person swipe to the right on devices that run LTR and let the person swipe from right to left on devices that run RTL
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Post post = mAdapter.getItem(position);
        Intent intent = new Intent(ACTION_DELETE_POST);
        intent.putExtra("position", position);
        intent.putExtra("post", Parcels.wrap(Post.class, post));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//        mAdapter.onSwipe(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float width = (float) viewHolder.itemView.getWidth();
            float alpha = 1.0f - Math.abs(dX) / width;
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Get RecyclerView item from the ViewHolder
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float width = (float) itemView.getWidth();
            float alpha = 1.0f - Math.abs(dX) / width;
            itemView.setAlpha(alpha);
            itemView.setTranslationX(dX);
            /* Set your color for positive displacement */

        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        L.m("clearView called");
        viewHolder.itemView.setAlpha(1.0F);
    }

    public interface OnSwipeListener {
        //Called when the user swipes an item from the RecyclerView at the given position
        void onSwipe(int position);
    }
}