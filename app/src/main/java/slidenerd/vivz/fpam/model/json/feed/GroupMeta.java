package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.GroupMetaRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 25/09/15.
 */
@Parcel(implementations = {GroupMeta.class},
        value = Parcel.Serialization.BEAN,
        analyze = {GroupMetaRealmProxy.class})
public class GroupMeta extends RealmObject {
    @PrimaryKey
    private String groupId;
    private long timestamp;

    public GroupMeta() {

    }

    public GroupMeta(String groupId, long timestamp) {
        this.groupId = groupId;
        this.timestamp = timestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}