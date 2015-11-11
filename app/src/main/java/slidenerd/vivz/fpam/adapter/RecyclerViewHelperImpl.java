package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewHelperImpl implements RecyclerViewHelper {

    private final RecyclerView mRecyclerView;

    public RecyclerViewHelperImpl(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public Context getContext() {
        return mRecyclerView.getContext();
    }

    @Override
    public int getWidth() {
        return mRecyclerView.getWidth();
    }

    @Override
    public int getChildCount() {
        return mRecyclerView.getChildCount();
    }

    @Override
    public void getLocationOnScreen(int[] locations) {
        mRecyclerView.getLocationOnScreen(locations);
    }

    @Override
    public View getChildAt(int index) {
        return mRecyclerView.getChildAt(index);
    }

    @Override
    public int getChildPosition(View child) {
        return mRecyclerView.getChildAdapterPosition(child);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mRecyclerView.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public void onTouchEvent(MotionEvent e) {
        mRecyclerView.onTouchEvent(e);
    }

    @Override
    public RecyclerView.OnScrollListener makeScrollListener(final RecyclerView.OnScrollListener l) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                l.onScrollStateChanged(null, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        };
    }
}