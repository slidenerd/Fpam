package slidenerd.vivz.fpam.core;

import io.realm.Realm;
import slidenerd.vivz.fpam.model.realm.Spammer;

/**
 * Created by vivz on 11/11/15.
 */
public class SpammerManager {
    public static final Spammer getInstance(Realm realm, String compositeUserGroupId) {
        Spammer spammer = realm.where(Spammer.class).equalTo("compositeUserGroupId", compositeUserGroupId).findFirst();
        if (spammer == null) {
            spammer = new Spammer();
            spammer.setCompositeUserGroupId(compositeUserGroupId);
        }
        return spammer;
    }
}
