package slidenerd.vivz.fpam.adapter;

import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class AbstractRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public static final int HEADER_COUNT = 1;
    public static final int FOOTER_COUNT = 1;

    //Our data source
    protected RealmResults<T> mResults;

    public AbstractRealmAdapter(Realm realm) {
        //load data from subclasses
        mResults = loadData(realm);
    }


    public int getHeaderCount() {
        return hasHeader() ? HEADER_COUNT : 0;
    }

    public int getFooterCount() {
        return hasFooter() ? FOOTER_COUNT : 0;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position < HEADER_COUNT;
        } else {
            return false;
        }
    }

    public boolean isFooter(int position) {
        if (hasFooter()) {
            return position >= getCount() + getHeaderCount();
        } else {
            return false;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
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

    /**
     * @param position the position within our adapter inclusive of headers,items and footers
     * @return an item only if it is not a header or a footer, otherwise returns null
     */
    public T getItem(int position) {
        if (!isHeader(position) && !isFooter(position) && !mResults.isEmpty()) {
            return mResults.get(position - getHeaderCount());
        }
        return null;
    }


    @Override
    public final int getItemCount() {
        return getHeaderCount() + getCount() + getFooterCount();
    }

    public final int getCount() {
        return mResults.size();
    }

    public abstract boolean hasHeader();

    public abstract boolean hasFooter();


    public void setData(RealmResults<T> results) {
        mResults = results;
        notifyItemRangeChanged(0, results.size());
    }

    protected abstract RealmResults<T> loadData(Realm realm);

    public enum ItemType {
        HEADER, ITEM, FOOTER;
    }
}