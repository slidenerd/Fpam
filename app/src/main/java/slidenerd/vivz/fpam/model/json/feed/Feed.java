package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.FeedRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 25/09/15.
 */
@Parcel(implementations = {Feed.class},
        value = Parcel.Serialization.BEAN,
        analyze = {FeedRealmProxy.class})
public class Feed extends RealmObject {
    @PrimaryKey
    private String groupId;
    private long timestamp;

    public Feed() {

    }

    public Feed(String groupId, long timestamp) {
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