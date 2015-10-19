package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.content.res.Resources;
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
import slidenerd.vivz.fpam.util.VersionUtils;

/**
 * Created by vivz on 05/10/15.
 */
public class SettingsGroupsAdapter extends AbstractRealmAdapter<Group, RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private View mHeaderView;
    private boolean mEnabled;
    private Context mContext;
    private Resources mResources;


    public SettingsGroupsAdapter(Context context, Realm realm, @NonNull RealmResults<Group> results) {
        super(context, realm, results);
        mContext = context;
        mResources = context.getResources();
        mInflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        if (headerView == null) {
            throw new IllegalArgumentException("Header View cannot be null for SettingsGroupsAdapter");
        }
        mHeaderView = headerView;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        notifyDataSetChanged();
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
            if (mHeaderView == null) {
                throw new IllegalArgumentException("Header View cannot be null for SettingsGroupsAdapter");
            }
            HeaderHolder holder = new HeaderHolder(mHeaderView);
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
            itemHolder.setGroupName(group.getName(), mEnabled);
            itemHolder.setMonitored(false, mEnabled);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckMonitored;
        private TextView mTextGroupName;

        public ItemHolder(View itemView) {
            super(itemView);
            mCheckMonitored = (CheckBox) itemView.findViewById(R.id.check_monitored);
            mTextGroupName = (TextView) itemView.findViewById(R.id.text_group_name);
        }

        public void setGroupName(String text, boolean enabled) {
            mTextGroupName.setText(text);
            int color;
            if (VersionUtils.isMarshmallowOrMore()) {
                color = mResources.getColor(enabled ? R.color.colorTextSecondary : R.color.colorTextSecondaryDisabled, mContext.getTheme());
            } else {
                color = mResources.getColor(enabled ? R.color.colorTextSecondary : R.color.colorTextSecondaryDisabled);
            }
            mTextGroupName.setTextColor(color);
        }

        public void setMonitored(boolean monitored, boolean enabled) {
            mCheckMonitored.setChecked(enabled ? monitored : false);
            mCheckMonitored.setEnabled(enabled);
        }
    }
}
