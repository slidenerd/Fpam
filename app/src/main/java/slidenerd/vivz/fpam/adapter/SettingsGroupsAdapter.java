package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 05/10/15.
 */
public class SettingsGroupsAdapter extends AbstractRealmAdapter<Group, RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Realm mRealm;

    public SettingsGroupsAdapter(Context context, Realm realm, @NonNull RealmResults<Group> results) {
        super(context, realm, results);
        mRealm = realm;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public boolean hasFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.ordinal()) {
            View view = mInflater.inflate(R.layout.header_groups, parent, false);
            HeaderHolder holder = new HeaderHolder(view);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.row_groups, parent, false);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            final Group group = getItem(position);
            itemHolder.setGroupName(group.getName());
            itemHolder.setMonitored(false);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckMonitored;
        private TextView mTextGroupName;

        public ItemHolder(View itemView) {
            super(itemView);
            mCheckMonitored = (CheckBox) itemView.findViewById(R.id.check_monitored);
            mTextGroupName = (TextView) itemView.findViewById(R.id.text_group_name);
        }

        public void setGroupName(String text) {
            mTextGroupName.setText(text);
        }

        public void setMonitored(boolean monitored) {
            mCheckMonitored.setChecked(monitored);
        }
    }
}
