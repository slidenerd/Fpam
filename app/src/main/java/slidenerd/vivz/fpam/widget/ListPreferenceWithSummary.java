package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ListPreferenceWithSummary extends ListPreference {
    public ListPreferenceWithSummary(final Context context) {
        this(context, null);
    }

    public ListPreferenceWithSummary(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setValue(final String value) {
        super.setValue(value);
        setSummary(value);
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(getEntry());
    }
}