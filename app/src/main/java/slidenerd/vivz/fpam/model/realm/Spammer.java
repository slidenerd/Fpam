package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 07/10/15.
 */

public class Spammer extends RealmObject {
    //This is a combination of the user id followed by the group id separated by a colon
    @PrimaryKey
    private String compositeUserGroupId;
    private String userName;
    private int spamCount;
    private long lastActive;
    private boolean authorized;

    //Default constructor must be declared if a custom constructor is included
    public Spammer() {

    }

    public Spammer(String compositeUserGroupId, String userName, int spamCount, long lastActive, boolean authorized) {
        this.compositeUserGroupId = compositeUserGroupId;

        this.userName = userName;
        this.spamCount = spamCount;
        this.lastActive = lastActive;
        this.authorized = authorized;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompositeUserGroupId() {
        return compositeUserGroupId;
    }

    public void setCompositeUserGroupId(String compositeUserGroupId) {
        this.compositeUserGroupId = compositeUserGroupId;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(int spamCount) {
        this.spamCount = spamCount;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
