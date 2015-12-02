package slidenerd.vivz.fpam.model.json;

import org.apache.commons.lang3.StringUtils;

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

    //Default constructor must be declared if a custom constructor is included
    public Group() {

    }

    public Group(String groupId, String groupName, String groupIcon, int unread, boolean monitored) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupIcon = groupIcon;
        this.unread = unread;
    }

    public static boolean isValidGroup(Group group) {
        return group != null && StringUtils.isNotBlank(group.getGroupId());
    }

    public static String toPrint(Group group) {
        return "Group{" +
                "groupId='" + group.groupId + '\'' +
                ", groupName='" + group.groupName + '\'' +
                ", groupIcon='" + group.groupIcon + '\'' +
                ", unread=" + group.unread +
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
}