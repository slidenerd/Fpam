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
    private ArrayList<KeywordGroup> mGroups;
    private SparseBooleanArray mChecked = new SparseBooleanArray();

    public AdapterKeywordGroups(Context context, ArrayList<KeywordGroup> groups) {

        mGroups = groups;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;
        if (row == null) {
            row = mInflater.inflate(R.layout.row_keyword_group, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.mTextGroupName.setText(mGroups.get(position).getGroupName());
        holder.mCheckGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChecked.put(position, isChecked);
                } else {
                    mChecked.delete(position);
                }
            }
        });
        holder.mCheckGroup.setChecked(mChecked.get(position) == true ? true : false);
        return row;
    }

    /**
     * Mark all the groups as selected by our Checkbox
     */
    public void selectAll() {
        for (int i = 0; i < mGroups.size(); i++) {
            mChecked.put(i, true);
        }
    }

    public void select(List<String> selected) {

        //Browse through the list of selected items
        for (int i = 0; i < selected.size(); i++) {

            //Browse through the list of stored group ids and names.
            for (int j = 0; j < mGroups.size(); j++) {

                //If a selected item's group id is the same as the stored item's group id, it means we need to select that group id
                if (StringUtils.equals(mGroups.get(j).getGroupId(), selected.get(i))) {
                    mChecked.put(j, true);
                    break;
                }
            }
        }
    }

    public boolean isAllSelected() {
        return mChecked.size() == mGroups.size();
    }

    public ArrayList<String> getSelected() {
        ArrayList<String> selected = new ArrayList<>();
        for (int i = 0; i < mGroups.size(); i++) {
            if (mChecked.get(i)) {
                selected.add(mGroups.get(i).getGroupId());
            }
        }
        return selected;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox mCheckGroup;
        TextView mTextGroupName;

        public ViewHolder(View view) {
            super(view);
            mCheckGroup = (CheckBox) view.findViewById(R.id.check_group);
            mTextGroupName = (TextView) view.findViewById(R.id.text_group_name);
        }
    }
}
