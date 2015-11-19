package slidenerd.vivz.fpam.extras;

import java.util.Comparator;

import slidenerd.vivz.fpam.model.realm.Frequency;

import static slidenerd.vivz.fpam.extras.Constants.GREATER;
import static slidenerd.vivz.fpam.extras.Constants.LESS;

/**
 * Created by vivz on 17/11/15.
 */
public class OccurrenceComparator implements Comparator<Frequency> {
    @Override
    public int compare(Frequency lhs, Frequency rhs) {

        if (rhs.getCount() < lhs.getCount()) {
            return LESS;
        } else if (rhs.getCount() > lhs.getCount()) {
            return GREATER;
        } else {
            return rhs.getText().compareTo(lhs.getText());
        }

    }
}
