package slidenerd.vivz.fpam.extras;

import java.util.Comparator;

import slidenerd.vivz.fpam.model.realm.TopKeywords;

import static slidenerd.vivz.fpam.extras.Constants.GREATER;
import static slidenerd.vivz.fpam.extras.Constants.LESS;

/**
 * Created by vivz on 17/11/15.
 */
public class FrequencyComparator implements Comparator<TopKeywords> {
    @Override
    public int compare(TopKeywords lhs, TopKeywords rhs) {

        if (rhs.getCount() < lhs.getCount()) {
            return LESS;
        } else if (rhs.getCount() > lhs.getCount()) {
            return GREATER;
        } else {
            return rhs.getCompositeGroupKeywordId().compareTo(lhs.getCompositeGroupKeywordId());
        }

    }
}
