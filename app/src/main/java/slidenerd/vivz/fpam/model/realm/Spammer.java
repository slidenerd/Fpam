package slidenerd.vivz.fpam.model.realm;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.SpammerRealmProxy;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.model.parcel.SpammerEntryParcelConverter;

/**
 * Created by vivz on 07/10/15.
 */
@Parcel(implementations = {SpammerRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Spammer.class})
public class Spammer extends RealmObject {
    @PrimaryKey
    private String userId;
    private String userName;
    private RealmList<SpammerEntry> entries = new RealmList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RealmList<SpammerEntry> getEntries() {
        return entries;
    }

    @ParcelPropertyConverter(SpammerEntryParcelConverter.class)
    public void setEntries(RealmList<SpammerEntry> entries) {
        this.entries = entries;
    }
}
