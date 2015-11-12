package slidenerd.vivz.fpam.core;

import io.realm.Realm;
import slidenerd.vivz.fpam.model.realm.Analytics;

/**
 * Created by vivz on 11/11/15.
 */
public class AnalyticsManager {


    public static final String GROUP_ID = "groupId";

    public static final Analytics getInstance(Realm realm, String groupId, String groupName) {
        Analytics analytics = realm.where(Analytics.class).equalTo(GROUP_ID, groupId).findFirst();
        if (analytics == null) {
            analytics = new Analytics(groupId, groupName, null, null, null);
        }
        return analytics;
    }

}
