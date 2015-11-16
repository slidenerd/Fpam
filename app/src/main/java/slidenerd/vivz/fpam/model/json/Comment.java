package slidenerd.vivz.fpam.model.json;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Comment extends RealmObject {

    @PrimaryKey
    private String commentId;
    private String userId;
    private String userName;
    private String message;
    private String created_time;


    public Comment() {

    }

    public Comment(String commentId, String userId, String userName, String message, String created_time) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.created_time = created_time;
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
     * @return The commentId
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * @param commentId The commentId
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}