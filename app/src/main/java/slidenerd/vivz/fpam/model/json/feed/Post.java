package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * TODO deal with attachments in some way
 */
@Parcel(implementations = {Post.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Post.class})
public class Post extends RealmObject {

    //The unqiue id of each post
    @PrimaryKey
    private String postId;

    private long rowId;

    //The user id of the person making the post
    private String userId;

    //The name of the person making the post
    private String userName;

    private String userPicture;

    //The message or content of the post if present, this is optional
    private String message;

    //The name of the link if present, this is optional
    private String name;

    //The caption of the link if present, this is optional
    private String caption;

    //The description of the link if present, this is optional
    private String description;

    //The picture if present in the post, this is optional
    private String picture;

    //The type of this post
    private String type;

    //The time this post was created
    private long createdTime;

    //The time this post was updated or edited or modified
    private long updatedTime;

    //The link if any present in this post, this is optional
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
     * @return The updatedTime
     */
    public long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * @param updatedTime The updatedTime
     */
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
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

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
}