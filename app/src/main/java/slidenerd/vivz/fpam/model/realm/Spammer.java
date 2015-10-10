package slidenerd.vivz.fpam.model.realm;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.SpammerRealmProxy;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 07/10/15.
 */
@Parcel(implementations = {SpammerRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Spammer.class})
public class Spammer extends RealmObject {
    //This is a combination of the user id followed by the group id separated by a colon
    @PrimaryKey
    private String userGroupCompositeId;
    private String userName;
    private int spamCount;
    private long timestamp;

    public Spammer() {

    }

    public Spammer(String userGroupCompositeId, String userName, int spamCount, long timestamp) {
        this.userGroupCompositeId = userGroupCompositeId;
        this.userName = userName;
        this.spamCount = spamCount;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGroupCompositeId() {
        return userGroupCompositeId;
    }

    public void setUserGroupCompositeId(String userGroupCompositeId) {
        this.userGroupCompositeId = userGroupCompositeId;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(int spamCount) {
        this.spamCount = spamCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}