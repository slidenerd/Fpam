package slidenerd.vivz.fpam.model.json;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

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

    //Tracks how many times a particular keyword was found while scanning the feed of a group
    private int count;

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

    public static String toString(Group group) {
        return "Group{" +
                "groupId='" + group.groupId + '\'' +
                ", groupName='" + group.groupName + '\'' +
                ", groupIcon='" + group.groupIcon + '\'' +
                ", unread=" + group.unread +
                ", lastLoaded=" + group.lastLoaded +
                ", monitored=" + group.monitored +
                ", count=" + group.count +
                '}';
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}