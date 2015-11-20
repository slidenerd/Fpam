package slidenerd.vivz.fpam.extras;

import java.util.Comparator;

import slidenerd.vivz.fpam.model.realm.TopKeywords;

import static slidenerd.vivz.fpam.extras.Constants.EQUAL;
import static slidenerd.vivz.fpam.extras.Constants.GREATER;
import static slidenerd.vivz.fpam.extras.Constants.LESS;

/**
 * Created by vivz on 17/11/15.
 */
public class FrequencyComparator implements Comparator<TopKeywords> {
    @Override
    public int compare(TopKeywords lhs, TopKeywords rhs) {

        if (lhs == null && rhs == null) {
            return EQUAL;
        } else if (lhs == null) {
            return GREATER;
        } else if (rhs == null) {
            return LESS;
        } else {
            if (rhs.getCount() < lhs.getCount()) {
                return LESS;
            } else if (rhs.getCount() > lhs.getCount()) {
                return GREATER;
            } else {
                return rhs.getKeyword().compareTo(lhs.getKeyword());
            }
        }
    }
}
