package slidenerd.vivz.fpam.model.json.group;

import org.parceler.Parcel;

import io.realm.GroupRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {GroupRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Group.class})
public class Group extends RealmObject {

    //The groupId of the group that uniquely identifies this group
    @PrimaryKey
    private String groupId;

    //groupName of the group
    private String groupName;

    //url for the group groupIcon
    private String groupIcon;

    private int unread;

    //Time when the feed for this group was last loaded
    private long lastLoaded;

    //Boolean indicating whether this group is actively monitored in the background from the Settings of the app
    private boolean monitored;

    //Default constructor must be declared if a custom constructor is included
    public Group() {

    }

    public Group(String groupId, String groupName, String groupIcon, int unread, long lastLoaded, boolean monitored) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupIcon = groupIcon;
        this.unread = unread;
        this.lastLoaded = lastLoaded;
        this.monitored = monitored;
    }


    /**
     * @return The groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName The groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return The groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return The groupIcon
     */
    public String getGroupIcon() {
        return groupIcon;
    }

    /**
     * @param groupIcon The groupIcon
     */
    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    /**
     * @return The unread
     */
    public int getUnread() {
        return unread;
    }

    /**
     * @param unread The unread
     */
    public void setUnread(int unread) {
        this.unread = unread;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }
}