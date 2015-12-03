package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.util.VersionUtils;

import static slidenerd.vivz.fpam.extras.Constants.KEY_MONITORED_PREFIX;

/**
 * Created by vivz on 05/10/15.
 */
public class SettingsGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Realm mRealm;
    private RealmResults<Group> mResults;
    private LayoutInflater mInflater;
    private View mHeaderView;
    private SharedPreferences mPref;

    //A variable to enable or disable all data. If the user chooses NEVER to monitor groups, all data are disabled, else enabled.

    private boolean mEnabled;
    private Context mContext;
    private Resources mResources;


    public SettingsGroupsAdapter(Context context, Realm realm, @NonNull RealmResults<Group> results) {
        mContext = context;
        mRealm = realm;
        mResults = results;
        mResources = context.getResources();
        mInflater = LayoutInflater.from(context);
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public int getItemCount() {
        return mResults.size() + 1;
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
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.ITEM.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (type == ItemType.HEADER.ordinal()) {
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
            final Group group = mResults.get(position - 1);
            itemHolder.setGroupName(group.getGroupName(), mEnabled);
            boolean isMonitored = mPref.getBoolean(KEY_MONITORED_PREFIX + group.getGroupId(), false);
            itemHolder.setMonitored(isMonitored, mEnabled);
        }
    }

    public enum ItemType {
        HEADER, ITEM;
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private CheckBox mCheckMonitored;
        private TextView mTextGroupName;

        public ItemHolder(View itemView) {
            super(itemView);
            mCheckMonitored = (CheckBox) itemView.findViewById(R.id.check_monitored);
            mTextGroupName = (TextView) itemView.findViewById(R.id.text_group_name);
            mCheckMonitored.setOnCheckedChangeListener(this);
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
            mCheckMonitored.setEnabled(enabled);
            mCheckMonitored.setChecked(enabled ? monitored : false);
        }

        /**
         * Whenever the user selects a group for background scan, update its monitored status accordingly.
         *
         * @param buttonView the checkbox that was selected
         * @param isChecked  whether the group is being monitored or not in the background
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Group group = mResults.get(getAdapterPosition() - 1);
            mPref.edit().putBoolean(KEY_MONITORED_PREFIX + group.getGroupId(), isChecked).apply();
        }
    }
}