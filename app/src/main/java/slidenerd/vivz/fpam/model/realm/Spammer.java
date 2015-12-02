package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 07/10/15.
 */

public class Spammer extends RealmObject {
    //This is a combination of the user id followed by the group id separated by a colon
    @PrimaryKey
    private String compositeGroupUserId;
    private String userName;
    private int count;
    private long lastActive;
    private boolean authorized;

    //Default constructor must be declared if a custom constructor is included
    public Spammer() {

    }

    public Spammer(String compositeGroupUserId, String userName, int count, long lastActive, boolean authorized) {
        this.compositeGroupUserId = compositeGroupUserId;

        this.userName = userName;
        this.count = count;
        this.lastActive = lastActive;
        this.authorized = authorized;
    }

    public static String toPrint(Spammer spammer) {
        return "Spammer{" +
                "compositeGroupUserId='" + spammer.compositeGroupUserId + '\'' +
                ", userName='" + spammer.userName + '\'' +
                ", count=" + spammer.count +
                ", lastActive=" + spammer.lastActive +
                ", authorized=" + spammer.authorized +
                '}';
    }

    public static String computeId(String groupId, String userId) {
        return groupId + ":" + userId;
    }

    public static String computeUserId(String compositeGroupUserId) {
        return compositeGroupUserId.substring(compositeGroupUserId.indexOf(':') + 1);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompositeGroupUserId() {
        return compositeGroupUserId;
    }

    public void setCompositeGroupUserId(String compositeGroupUserId) {
        this.compositeGroupUserId = compositeGroupUserId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
