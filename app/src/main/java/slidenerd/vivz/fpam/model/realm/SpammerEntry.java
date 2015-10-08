package slidenerd.vivz.fpam.model.realm;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.SpammerEntryRealmProxy;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 07/10/15.
 */
@Parcel(implementations = {SpammerEntryRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {SpammerEntry.class})
public class SpammerEntry extends RealmObject {
    @PrimaryKey
    private String groupId;
    private String groupName;
    private int spamCount;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(int spamCount) {
        this.spamCount = spamCount;
    }
}
