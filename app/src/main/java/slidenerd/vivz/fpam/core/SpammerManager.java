package slidenerd.vivz.fpam.core;

import io.realm.Realm;
import slidenerd.vivz.fpam.model.realm.Spammer;

/**
 * Created by vivz on 11/11/15.
 */
public class SpammerManager {

    public static final String COMPOSITE_ID = "compositeUserGroupId";

    public static final Spammer getInstance(Realm realm, String compositeUserGroupId) {
        Spammer spammer = realm.where(Spammer.class).equalTo(COMPOSITE_ID, compositeUserGroupId).findFirst();
        if (spammer == null) {
            spammer = new Spammer();
            spammer.setCompositeUserGroupId(compositeUserGroupId);
        }
        return spammer;
    }

    public static final Spammer getSpammer(Realm realm, String userId) {
        return realm.where(Spammer.class).beginsWith(COMPOSITE_ID, userId).findFirst();
    }

}
