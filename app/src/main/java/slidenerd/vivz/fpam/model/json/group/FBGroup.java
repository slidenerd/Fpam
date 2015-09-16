package slidenerd.vivz.fpam.model.json.group;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Windows on 13-02-2015.
 */

public class FBGroup extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("icon")
    private String iconUrl;
    @SerializedName("administrator")
    private boolean isAdministrator;
    @SerializedName("unread")
    private int unreadCount;

    public FBGroup() {
    }

    public FBGroup(long id, String name, boolean isAdministrator, String iconUrl, int unreadCount) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.isAdministrator = isAdministrator;
        this.unreadCount = unreadCount;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean getIsAdministrator() {
        return isAdministrator;
    }

    public void setIsAdministrator(boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }
}
