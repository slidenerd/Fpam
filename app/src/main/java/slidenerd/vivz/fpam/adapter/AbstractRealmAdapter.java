package slidenerd.vivz.fpam.adapter;

import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class AbstractRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements OnSwipeListener {
    protected RealmResults<T> mRealmResults;
    private Realm realm;

    public AbstractRealmAdapter(Realm realm) {
        this.realm = realm;
        this.mRealmResults = getResults(realm);
    }

    public int getHeaderCount() {
        return hasHeader() ? 1 : 0;
    }

    public int getFooterCount() {
        return hasFooter() ? 1 : 0;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position == 0;
        } else {
            return false;
        }
    }

    public boolean isFooter(int position) {
        if (hasFooter()) {
            return position >= getItemCount() + getHeaderCount() ? true : false;
        } else {
            return false;
        }
    }

    @Override
    public long getItemId(int i) {
        // TODO: find better solution once we have unique IDs
        return i;
    }

    public T getItem(int i) {
        return mRealmResults.get(i);
    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return ItemType.HEADER.ordinal();
        } else if (isFooter(position)) {
            return ItemType.FOOTER.ordinal();
        } else {
            return ItemType.ITEM.ordinal();
        }
    }

    @Override
    public final int getItemCount() {
        return getHeaderCount() + getCount() + getFooterCount();
    }

    public final int getCount() {
        return mRealmResults.size();
    }

    public void add(T item, boolean update) {
        realm.beginTransaction();
        T phraseToWrite = update == true ? realm.copyToRealmOrUpdate(item) : realm.copyToRealm(item);
        realm.commitTransaction();
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public final void onSwipe(int position) {
        if (!isHeader(position) && !isFooter(position)) {
            int itemPosition = position - getHeaderCount();
            if (!mRealmResults.isEmpty()) {
                realm.beginTransaction();
                T item = mRealmResults.get(itemPosition);
                item.removeFromRealm();
                realm.commitTransaction();
                notifyItemRemoved(position);
            }
        }
    }

    public abstract boolean hasHeader();

    public abstract boolean hasFooter();

    public abstract RealmResults<T> getResults(Realm realm);

    public enum ItemType {
        HEADER, ITEM, FOOTER;
    }
}