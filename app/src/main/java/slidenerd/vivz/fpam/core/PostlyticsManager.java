package slidenerd.vivz.fpam.core;

import io.realm.Realm;
import slidenerd.vivz.fpam.model.realm.Postlytics;
import slidenerd.vivz.fpam.util.ModelUtils;

/**
 * Created by vivz on 12/11/15.
 */
public class PostlyticsManager {
    private static final String DAILYTICS_ID = "compositeGroupDateId";

    public static final Postlytics getInstance(Realm realm, String groupId) {
        String compositeGroupDateId = ModelUtils.computeDailyticsId(groupId);
        Postlytics postlytics = realm.where(Postlytics.class).equalTo(DAILYTICS_ID, compositeGroupDateId).findFirst();
        if (postlytics == null) {
            postlytics = new Postlytics();
            postlytics.setCompositeGroupDateId(compositeGroupDateId);
        }
        return postlytics;
    }

    public static final void updateDeleted(Postlytics postlytics, Realm realm, int deleted) {
        realm.beginTransaction();
        postlytics.setDeleted(postlytics.getDeleted() + deleted);
        realm.commitTransaction();
    }

    public static final void updateEmpty(Postlytics postlytics, Realm realm, int deletedEmpty) {
        realm.beginTransaction();
        postlytics.setDeletedEmpty(postlytics.getDeletedEmpty() + deletedEmpty);
        realm.commitTransaction();
    }

    public static final void updateFailed(Postlytics postlytics, Realm realm, int failed) {
        realm.beginTransaction();
        postlytics.setFailed(postlytics.getDeletedEmpty() + failed);
        realm.commitTransaction();
    }

}
