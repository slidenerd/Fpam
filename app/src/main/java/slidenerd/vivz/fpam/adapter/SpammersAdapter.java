package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.realm.Spammer;

/**
 * TODO complete this adapter
 * Created by vivz on 29/08/15.
 */
public class SpammersAdapter extends AbstractRealmAdapter<Spammer, SpammersAdapter.ItemHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public SpammersAdapter(Context context, Realm realm, RealmResults<Spammer> results) {
        super(context, realm, results);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

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
        View view = mLayoutInflater.inflate(R.layout.row_spammer, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Spammer spammer = getItem(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);

        }
    }
}