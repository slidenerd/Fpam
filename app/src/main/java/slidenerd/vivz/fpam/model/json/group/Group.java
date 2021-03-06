package slidenerd.vivz.fpam.model.json.group;

import org.parceler.Parcel;

import io.realm.GroupRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {GroupRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Group.class})
public class Group extends RealmObject {

    @PrimaryKey
    private String id;

    private String name;

    private String icon;

    private int unread;

    public Group() {

    }

    public Group(String id, String name, String icon, int unread) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.unread = unread;
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

}