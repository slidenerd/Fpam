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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.util.VersionUtils;

import static slidenerd.vivz.fpam.extras.Constants.KEY_MONITORED_PREFIX;

/**
 * Created by vivz on 05/10/15.
 */
public class AdapterGroups extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * The type of the item for a header that lets the user pick monitoring frequency
     */
    public static final int HEADER = 1;
    /**
     * The type of the item for a group
     */
    public static final int ITEM = 2;
    /**
     * The color of each item in the Adapter when the item is enabled.
     */
    private int mColorEnabled;
    /**
     * The color of each item in the Adapter when the item is disabled.
     */
    private int mColorDisabled;
    /**
     * The list of Groups to be displayed
     */
    private RealmResults<Group> mGroups;
    private LayoutInflater mInflater;
    /**
     * The header allowing the user to pick a value as monitoring frequency.
     * The user can choose to disable scanning if needed.
     */
    private View mHeader;
    private SharedPreferences mPref;

    /**
     * A variable to enable or disable all items. If the user chooses to disable monitoring groups, all items in the Adapter are disabled, else enabled.
     */
    private boolean mEnabled;

    /**
     * Instantiates a new Adapter groups.
     * Initialize the colors for enabled and disabled state of each item.
     *
     * @param context the context
     * @param groups  the groups
     */
    public AdapterGroups(Context context, @NonNull RealmResults<Group> groups) {
        mGroups = groups;
        mInflater = LayoutInflater.from(context);
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        if (VersionUtils.isMarshmallowOrMore()) {
            mColorEnabled = res.getColor(R.color.colorTextSecondary, context.getTheme());
            mColorDisabled = res.getColor(R.color.colorTextSecondaryDisabled, context.getTheme());
        } else {
            mColorEnabled = res.getColor(R.color.colorTextSecondary);
            mColorDisabled = res.getColor(R.color.colorTextSecondaryDisabled);
        }
    }

    /**
     * Sets header view.
     *
     * @param headerView the header view
     */
    public void setHeaderView(View headerView) {
        if (headerView == null) {
            throw new IllegalArgumentException("Header View cannot be null for AdapterGroups");
        }
        mHeader = headerView;
    }

    /**
     * Sets the item to be enabled or disabled depending on the parameter's value.
     *
     * @param enabled Boolean indicating whether all group items should be enabled or disabled. When the user opts to disable background scanning, all items are disabled.
     */
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        notifyDataSetChanged();
    }

    /**
     * @param position Position to query.
     * @return Integer value identifying the type of the view needed to represent the item at position. At the zeroth position, we have a header and at any other position, we have an item.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter which is the sum of the number of groups and a single header.
     */
    @Override
    public int getItemCount() {
        return mGroups.size() + 1;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item. The type can be either a header or an item containing group information.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param type   The view type of the new View. The type may be a header or an item that contains groups information.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (type == HEADER) {
            HeaderHolder holder = new HeaderHolder(mHeader);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.row_groups, parent, false);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     * If our current position has a View that represents an item, we get the details of that item which contains group information such as name and whether the group is being actively monitored.
     * The boolean storing whether a particular group id is monitored or not has a key that starts with KEY_MONITORED_PREFIX followed by the group id of the group under consideration.
     * Set the group name along with the boolean that indicates whether the item is enabled or disabled. If the user has opted never to scan any groups which is the default choice when the app is freshly installed, monitoring is disabled and hence all group items are disabled.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;

            //Account for the header which is present at position 0
            final Group group = mGroups.get(position - 1);
            itemHolder.setGroupName(group.getGroupName(), mEnabled);
            boolean isMonitored = mPref.getBoolean(KEY_MONITORED_PREFIX + group.getGroupId(), false);
            itemHolder.setMonitored(isMonitored, mEnabled);
        }
    }

    /**
     * The ViewHolder for caching Header items.
     */
    public class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * The ViewHolder for caching group items.
     */
    public class ItemHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private CheckBox mMonitored;
        private TextView mGroupName;

        public ItemHolder(View itemView) {
            super(itemView);
            mMonitored = (CheckBox) itemView.findViewById(R.id.monitored);
            mGroupName = (TextView) itemView.findViewById(R.id.group_name);
            mMonitored.setOnCheckedChangeListener(this);
        }

        /**
         * Sets group name. If the group is enabled, it is displayed using the text color for an enabled item
         *
         * @param groupName The name of the group to be displayed
         * @param enabled   Boolean indicating whether this item is enabled or disabled. Depending on whether the item is enabled or disabled, we change the text color accordingly.
         */
        public void setGroupName(String groupName, boolean enabled) {
            mGroupName.setText(groupName);
            mGroupName.setTextColor(enabled ? mColorEnabled : mColorDisabled);
        }

        /**
         * Sets monitored. If the item is enabled, then the user can check or uncheck a particular group item.
         * If the item is disabled, all selections are cleared and the user cannot check any items
         *
         * @param monitored Boolean indicating whether this group should be monitored in the background for spam or not.
         * @param enabled   Boolean indicating whether this item is enabled or not. If the item is enabled, let the user select or deselect the monitored option.
         */
        public void setMonitored(boolean monitored, boolean enabled) {
            mMonitored.setEnabled(enabled);
            mMonitored.setChecked(enabled ? monitored : false);
        }

        /**
         * Whenever the user selects a group for background scan, update its monitored status accordingly.
         *
         * @param cb        The checkbox that was selected
         * @param isChecked Boolean indicating whether the group is being monitored or not in the background
         */
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean isChecked) {

            //Account for the header at position 0
            Group group = mGroups.get(getAdapterPosition() - 1);

            //Mark the group id as monitored
            mPref.edit().putBoolean(KEY_MONITORED_PREFIX + group.getGroupId(), isChecked).apply();
        }
    }
}
