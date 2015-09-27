package slidenerd.vivz.fpam.event;

/**
 * Created by vivz on 27/09/15.
 */
public class EventGroupSelected {
    private String groupId;
    public EventGroupSelected(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
