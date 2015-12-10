package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.pojo.KeywordGroup;

/**
 * Created by vivz on 03/11/15.
 */
public class AdapterKeywordGroups extends BaseAdapter {
    private LayoutInflater mInflater;
    /**
     * The list of groups to be displayed inside this Adapter
     */
    private ArrayList<KeywordGroup> mGroups;

    /**
     * Stores key as the position of the item in the list and value as true if the item is selected. Does not store a key with false value.
     */
    private SparseBooleanArray mChecked = new SparseBooleanArray();

    /**
     * Instantiates a new Adapter keyword groups.
     *
     * @param context the context
     * @param groups  the groups
     */
    public AdapterKeywordGroups(Context context, ArrayList<KeywordGroup> groups) {
        mGroups = groups;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = mInflater.inflate(R.layout.row_keyword_group, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.mGroupName.setText(mGroups.get(position).getGroupName());
        holder.mSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    //If an item is checked by the user, add it to the list of checked items with position as the key
                    mChecked.put(position, isChecked);
                } else {

                    //If an item is unchecked by the user, delete it from the list of checked items using position as the key
                    mChecked.delete(position);
                }
            }
        });
        holder.mSelected.setChecked(mChecked.get(position));
        return row;
    }

    /**
     * Mark all the groups as selected by the user
     */
    public void selectAll() {
        for (int i = 0; i < mGroups.size(); i++) {
            mChecked.put(i, true);
        }
    }

    /**
     * Select.
     *
     * @param selected List of selected Group Ids.
     */
    public void select(List<String> selected) {

        //Browse through the list of selected data
        for (int i = 0; i < selected.size(); i++) {

            //Browse through the list of stored group ids and names.
            for (int j = 0; j < mGroups.size(); j++) {

                //If a selected data's group id is the same as the stored data's group id, it means we need to select that group id
                if (StringUtils.equals(mGroups.get(j).getGroupId(), selected.get(i))) {
                    mChecked.put(j, true);
                    break;
                }
            }
        }
    }

    /**
     * Is all selected boolean.
     *
     * @return Boolean indicating if all the groups are selected.
     */
    public boolean isAllSelected() {
        return mChecked.size() == mGroups.size();
    }

    /**
     * Gets selected.
     *
     * @return List of Group Ids selected by the user.
     */
    public ArrayList<String> getSelected() {
        ArrayList<String> selected = new ArrayList<>();
        for (int i = 0; i < mGroups.size(); i++) {

            //If the value for a given position is true, that item is selected by the user.
            if (mChecked.get(i)) {
                selected.add(mGroups.get(i).getGroupId());
            }
        }
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox mSelected;
        private TextView mGroupName;

        public ViewHolder(View view) {
            super(view);
            mSelected = (CheckBox) view.findViewById(R.id.selected);
            mGroupName = (TextView) view.findViewById(R.id.group_name);
        }
    }
}
