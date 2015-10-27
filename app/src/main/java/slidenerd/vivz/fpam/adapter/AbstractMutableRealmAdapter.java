package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class AbstractMutableRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends AbstractRealmAdapter<T, VH> implements SwipeHelper.OnSwipeListener {

    private Realm mRealm;

    public AbstractMutableRealmAdapter(Context context, Realm realm, RealmResults<T> results) {
        super(context, realm, results);
        this.mRealm = realm;
    }

    public void add(T item, boolean update) {
        mRealm.beginTransaction();
        T phraseToWrite = (update == true) ? mRealm.copyToRealmOrUpdate(item) : mRealm.copyToRealm(item);
        mRealm.commitTransaction();
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public final void onSwipe(int position) {
        if (!isHeader(position) && !isFooter(position)) {
            int itemPosition = position - getHeaderCount();
            if (!mResults.isEmpty()) {
                mRealm.beginTransaction();
                T item = mResults.get(itemPosition);
                item.removeFromRealm();
                mRealm.commitTransaction();
                notifyDataSetChanged();
            }
        }
    }

}