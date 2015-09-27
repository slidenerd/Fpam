package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class AbstractMutableRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
        extends AbstractRealmAdapter<T, VH> implements OnSwipeListener {

    private Realm realm;

    public AbstractMutableRealmAdapter(Context context, Realm realm, RealmResults<T> results) {
        super(context, realm, results);
        this.realm = realm;
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
            if (!mResults.isEmpty()) {
                realm.beginTransaction();
                T item = mResults.get(itemPosition);
                item.removeFromRealm();
                realm.commitTransaction();
                notifyItemRemoved(position);
            }
        }
    }

}