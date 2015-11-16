package slidenerd.vivz.fpam.model.json;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Comments extends RealmObject {
    @PrimaryKey
    private String postId;
    private RealmList<Comment> data = new RealmList<>();
    private String before;
    private String after;

    public Comments() {

    }

    public static String toString(Comments comments) {
        return "Comments{" +
                "postId='" + comments.postId + '\'' +
                ", data=" + comments.data +
                ", before='" + comments.before + '\'' +
                ", after='" + comments.after + '\'' +
                '}';
    }

    /**
     * @return The data
     */
    public RealmList<Comment> getData() {
        return data;
    }

    /**
     * @param data The data
     */

    public void setData(RealmList<Comment> data) {
        this.data = data;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}