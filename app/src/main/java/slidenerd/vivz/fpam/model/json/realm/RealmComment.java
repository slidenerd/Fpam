package slidenerd.vivz.fpam.model.json.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 17/09/15.
 */
public class RealmComment extends RealmObject {
    @PrimaryKey
    private String id;

    private RealmUser from;

    private String message;

    private String createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmUser getFrom() {
        return from;
    }

    public void setFrom(RealmUser from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
