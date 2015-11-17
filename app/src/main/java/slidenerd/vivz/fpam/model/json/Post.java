package slidenerd.vivz.fpam.model.json;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * TODO deal with attachments in some way
 */

public class Post extends RealmObject {

    //The unqiue id of each post
    @PrimaryKey
    private String postId;

    //the group id of the group where this Post was made.
    private String groupId;

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

    //Must have default constructor if a custom constructor is included
    public Post() {

    }

    public static String toPrint(Post post) {
        return "Post{" +
                "postId='" + post.postId + '\'' +
                ", groupId='" + post.groupId + '\'' +
                ", rowId=" + post.rowId +
                ", userId='" + post.userId + '\'' +
                ", userName='" + post.userName + '\'' +
                ", userPicture='" + post.userPicture + '\'' +
                ", message='" + post.message + '\'' +
                ", name='" + post.name + '\'' +
                ", caption='" + post.caption + '\'' +
                ", description='" + post.description + '\'' +
                ", picture='" + post.picture + '\'' +
                ", type='" + post.type + '\'' +
                ", createdTime=" + post.createdTime +
                ", updatedTime=" + post.updatedTime +
                ", link='" + post.link + '\'' +
                '}';
    }

    public static long computeRowId(String postId) {
        long rowId = -1;
        int index = postId.indexOf('_');
        if (index != -1) {
            String suffix = postId.substring(index + 1, postId.length());
            try {
                rowId = Long.parseLong(suffix);
            } catch (NumberFormatException ignore) {
            }
        }
        return rowId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
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

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}