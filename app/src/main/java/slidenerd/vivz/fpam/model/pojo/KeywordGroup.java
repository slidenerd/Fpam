package slidenerd.vivz.fpam.model.pojo;

/**
 * Created by vivz on 23/11/15.
 */
public class KeywordGroup {
    private String groupId;
    private String groupName;

    public KeywordGroup(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
