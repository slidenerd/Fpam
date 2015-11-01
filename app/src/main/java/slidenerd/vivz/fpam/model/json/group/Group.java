package slidenerd.vivz.fpam.model.json.group;

import org.parceler.Parcel;

import io.realm.GroupRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {GroupRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Group.class})
public class Group extends RealmObject {

    //The id of the group that uniquely identifies this group
    @PrimaryKey
    private String id;

    //name of the group
    private String name;

    //url for the group icon
    private String icon;

    private int unread;

    //Time when the feed for this group was last loaded
    private long timestamp;

    //Boolean indicating whether this group is actively monitored in the background from the Settings of the app
    private boolean monitored;

    public Group() {

    }

    public Group(String id, String name, String icon, int unread, long timestamp, boolean monitored) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.unread = unread;
        this.timestamp = timestamp;
        this.monitored = monitored;
    }


    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }
}