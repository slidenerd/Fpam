package slidenerd.vivz.fpam.settings;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.log.L;

/**
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.settings_general)
public class SettingsFragmentGeneral extends Fragment {
    @Pref
    MyPrefs_ mPref;
    @StringRes(R.string.text_summary_swipe_delete_on)
    String mDeleteOn;
    @StringRes(R.string.text_summary_swipe_delete_off)
    String mDeleteOff;
    @StringArrayRes(R.array.cache_size_entries)
    String[] cacheEntries;
    @ViewById(R.id.text_summary_cache)
    TextView mTextSummaryCache;
    @ViewById(R.id.text_summary_confirm_deletion)
    TextView mTextSummarySwipeDelete;
    @ViewById(R.id.switch_confirm_deletion)
    Switch mSwitchDelete;
    boolean mSwipeToDelete;
    int mStoredCacheSize;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @AfterViews
    void onViewCreated() {
        mSwipeToDelete = mPref.swipeToDelete().get();
        mStoredCacheSize = mPref.cacheSize().get();
        mTextSummaryCache.setText(mStoredCacheSize + "");
        mSwitchDelete.setChecked(mSwipeToDelete);
        mTextSummarySwipeDelete.setText(mSwipeToDelete ? mDeleteOn : mDeleteOff);
    }

    @Click({R.id.text_cache, R.id.text_summary_cache, R.id.icon_cache})
    void onClickCacheSize() {

        int selectedIndex = -1;
        for (int i = 0; i < cacheEntries.length; i++) {
            if (cacheEntries[i].equals(mStoredCacheSize + "")) {
                selectedIndex = i;
            }
        }

        new MaterialDialog.Builder(mContext)
                .title(R.string.title_post_cache_size)
                .items(R.array.cache_size_entries)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                        L.t(mContext, "item selected at " + position);
                        mPref.cacheSize().put(Integer.parseInt(cacheEntries[position]));
                        mTextSummaryCache.setText(cacheEntries[position]);
                        return true;
                    }
                })
                .build()
                .show();
    }

    @Click({R.id.text_confirm_deletion, R.id.text_summary_confirm_deletion, R.id.icon_delete})
    void onClickSwipeDelete() {
        mSwitchDelete.toggle();
        updateSummarySwipeToDelete();
    }

    @CheckedChange(R.id.switch_confirm_deletion)
    void onChangeSwipeDelete() {
        updateSummarySwipeToDelete();
    }

    void updateSummarySwipeToDelete() {
        mSwipeToDelete = mSwitchDelete.isChecked();
        L.t(mContext, mSwipeToDelete + "");
        mPref.swipeToDelete().put(mSwipeToDelete);
        mTextSummarySwipeDelete.setText(mSwipeToDelete ? mDeleteOn : mDeleteOff);
    }

}
