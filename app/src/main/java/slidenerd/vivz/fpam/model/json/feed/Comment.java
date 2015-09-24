package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.CommentRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {CommentRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Comment.class})
public class Comment extends RealmObject {

    private User from;
    private String message;
    @PrimaryKey
    private String id;
    private String created_time;


    public Comment() {

    }

    public Comment(String id, String message, User from, String created_time) {
        this.from = from;
        this.message = message;
        this.id = id;
        this.created_time = created_time;
    }

    /**
     * @return The from
     */
    public User getFrom() {
        return from;
    }

    /**
     * @param from The from
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
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

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }
}