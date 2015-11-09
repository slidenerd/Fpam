package slidenerd.vivz.fpam.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 03/11/15.
 */
public class AdapterKeywordGroups extends RealmBaseAdapter<Group> {

    private LayoutInflater mInflater;
    private RealmResults<Group> mResults;
    private SparseBooleanArray mChecked = new SparseBooleanArray();

    public AdapterKeywordGroups(Context context, RealmResults<Group> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        mResults = realmResults;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean allSelected() {
        return mChecked.size() == mResults.size();
    }

    public void selectAll() {
        for (int i = 0; i < mResults.size(); i++) {
            mChecked.put(i, true);
        }
    }

    public void select(RealmList<Group> selectedItems) {
        for (int i = 0; i < selectedItems.size(); i++) {
            for (int j = 0; j < mResults.size(); j++) {
                if (selectedItems.get(i).getGroupId().equals(mResults.get(j).getGroupId())) {
                    mChecked.put(j, true);
                    break;
                }
            }
        }
    }

    public RealmList<Group> getSelected() {
        RealmList<Group> checkedItems = new RealmList<>();
        for (int i = 0; i < mResults.size(); i++) {
            if (mChecked.get(i)) {
                checkedItems.add(mResults.get(i));
            }
        }
        return checkedItems;
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
        holder.mTextGroupName.setText(mResults.get(position).getGroupName());
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

    public static class ViewHolder {

        CheckBox mCheckGroup;
        TextView mTextGroupName;

        public ViewHolder(View view) {
            mCheckGroup = (CheckBox) view.findViewById(R.id.check_group);
            mTextGroupName = (TextView) view.findViewById(R.id.text_group_name);
        }
    }
}
