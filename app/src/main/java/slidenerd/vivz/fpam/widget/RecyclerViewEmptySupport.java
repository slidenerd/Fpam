package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vivz on 24/09/15.
 */
public class RecyclerViewEmptySupport extends RecyclerView {
    private View mEmptyView;
    private MyAdapterDataObserver mObserver = new MyAdapterDataObserver();

    public RecyclerViewEmptySupport(Context context) {
        super(context);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
            mObserver.onChanged();
        }
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void checkIfEmpty() {
        Adapter adapter = getAdapter();
        if (adapter != null) {
            if (adapter.getItemCount() == 0 && mEmptyView != null) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    public class MyAdapterDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            checkIfEmpty();
        }
    }
}