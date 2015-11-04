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

    public static Spammer NONE = new Spammer("NONE", "anonymous", 0, 0, false);
    //This is a combination of the user id followed by the group id separated by a colon
    @PrimaryKey
    private String userGroupCompositeId;
    private String userName;
    private int spamCount;
    private long timestamp;
    private boolean allowed;

    public Spammer() {

    }

    public Spammer(String userGroupCompositeId, String userName, int spamCount, long timestamp, boolean allowed) {
        this.userGroupCompositeId = userGroupCompositeId;
        this.userName = userName;
        this.spamCount = spamCount;
        this.timestamp = timestamp;
        this.allowed = allowed;
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

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
