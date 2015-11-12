package slidenerd.vivz.fpam.core;

import io.realm.Realm;
import slidenerd.vivz.fpam.model.realm.Dailytics;

/**
 * Created by vivz on 12/11/15.
 */
public class DailyticsManager {
    private static final String DAILYTICS_ID = "compositeGroupDateId";

    public static final Dailytics getInstance(Realm realm, String compositeGroupDateId) {
        Dailytics dailytics = realm.where(Dailytics.class).equalTo(DAILYTICS_ID, compositeGroupDateId).findFirst();
        if (dailytics == null) {
            dailytics = new Dailytics();
            dailytics.setCompositeGroupDateId(compositeGroupDateId);
        }
        return dailytics;
    }

    public static final void updateDeleted(Dailytics dailytics, Realm realm, int deleted) {
        realm.beginTransaction();
        dailytics.setDeleted(dailytics.getDeleted() + deleted);
        realm.commitTransaction();
    }

    public static final void updateEmpty(Dailytics dailytics, Realm realm, int deletedEmpty) {
        realm.beginTransaction();
        dailytics.setDeletedEmpty(dailytics.getDeletedEmpty() + deletedEmpty);
        realm.commitTransaction();
    }

    public static final void updateFailed(Dailytics dailytics, Realm realm, int failed) {
        realm.beginTransaction();
        dailytics.setFailed(dailytics.getDeletedEmpty() + failed);
        realm.commitTransaction();
    }

}
