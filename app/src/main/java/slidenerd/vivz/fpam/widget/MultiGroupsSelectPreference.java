package slidenerd.vivz.fpam.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 03/08/15.
 */
public class MultiGroupsSelectPreference extends MultiSelectListPreference {
    private ArrayList<Group> mListGroups = new ArrayList<>();
    private Context mContext;
    private Realm realm;

    public MultiGroupsSelectPreference(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MultiGroupsSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        realm = Realm.getInstance(mContext);
        mListGroups = DataStore.loadGroups(realm);
        ArrayList<String> listGroupNames = new ArrayList<>(mListGroups.size());
        ArrayList<String> listGroupIds = new ArrayList<>(mListGroups.size());
        for (Group group : mListGroups) {
            listGroupIds.add(group.getId());
            listGroupNames.add(group.getName());
        }
        setEntries(listGroupNames.toArray(new CharSequence[listGroupNames.size()]));
        setEntryValues(listGroupIds.toArray(new CharSequence[listGroupIds.size()]));
        realm.close();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        if (mListGroups.isEmpty()) {
            builder.setMessage(R.string.text_no_groups);
        } else {
            super.onPrepareDialogBuilder(builder);
        }
    }
}