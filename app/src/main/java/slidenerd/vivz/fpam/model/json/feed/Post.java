package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {Post.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Post.class})
public class Post extends RealmObject {

    @PrimaryKey
    private String postId;

    private String userId;

    private String userName;

    private String message;

    private String name;

    private String caption;

    private String description;

    private String picture;

    private String type;

    private String updated_time;

    private String link;

    public Post() {

    }

    /**
     * @return The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption The caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The updated_time
     */
    public String getUpdated_time() {
        return updated_time;
    }

    /**
     * @param updated_time The updated_time
     */
    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     * @param postId The postId
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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