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
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * Created by vivz on 30/09/15.
 */
@EFragment(R.layout.settings_general)
public class SettingsFragmentGeneral extends Fragment {
    @Pref
    MyPrefs_ mPref;

    //The number of posts to be stored in the database for offline access for each group

    @StringArrayRes(R.array.pref_cache_sizes)
    String[] mCacheSizes;

    //The textview controlling the summary to be displayed when the user has selected the number of posts to be stored in the database

    @ViewById(R.id.text_summary_cache)
    TextView mTextSummaryCache;

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

        //Load the value of the setting that indicates whether the user should delete posts on swipe

        boolean shouldDelete = mPref.swipeToDelete().get();

        //Load the number of posts to be stored in the database for each group

        int cacheSize = mPref.cacheSize().get();

        //Initialize views based on setting values

        mTextSummaryCache.setText(getString(R.string.pref_summary_cache, cacheSize + ""));
    }

    /**
     * Called when you click anywhere , icon or title or summary related to cache size in the first tab 'General' in the Settings screen
     */
    @Click({R.id.text_cache, R.id.text_summary_cache, R.id.icon_cache})
    void onClickCacheSize() {

        //Load the number of posts to be stored in the database for each group

        int cacheSize = mPref.cacheSize().get();

        //Let the first item be selected by default

        int selectedIndex = 0;
        for (int i = 0; i < mCacheSizes.length; i++) {
            if (mCacheSizes[i].equals(cacheSize + "")) {

                //Update the selected index based on what the person has actually selected.

                selectedIndex = i;
            }
        }

        //Display a dialog containing the user choices for the number of items to be stored in the database

        new MaterialDialog.Builder(mContext)
                .title(R.string.pref_cache)
                .items(R.array.pref_cache_sizes)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {

                        //Store the new value of cache size selected by the user in the SharedPreferences

                        mPref.cacheSize().put(Integer.parseInt(mCacheSizes[position]));

                        //Update the summary to reflect the new value of cache size selected by the user

                        mTextSummaryCache.setText(getString(R.string.pref_summary_cache, mCacheSizes[position]));
                        return true;
                    }
                })
                .build()
                .show();
    }

    @Click({R.id.text_keywords, R.id.text_summary_keywords, R.id.icon_keywords})
    void onClickKeywords() {
        NavUtils.startActivityKeywords(mContext);
    }


}
