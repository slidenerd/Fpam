package slidenerd.vivz.fpam.model.realm;

import org.parceler.Parcel;

import io.realm.GroupMetaDataRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 25/09/15.
 */
@Parcel(implementations = {GroupMetaData.class},
        value = Parcel.Serialization.BEAN,
        analyze = {GroupMetaDataRealmProxy.class})
public class GroupMetaData extends RealmObject {
    @PrimaryKey
    private String groupId;
    private long timestamp;
    private boolean monitored;

    public GroupMetaData() {

    }

    public GroupMetaData(String groupId, long timestamp, boolean monitored) {
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.monitored = monitored;
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

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }
}