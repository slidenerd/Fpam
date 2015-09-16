package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FBPost implements Serializable {
    @SerializedName("id")
    private String postId;

    @SerializedName("picture")
    private String picture;

    @SerializedName("message")
    private String message;

    @SerializedName("description")
    private String description;

    @SerializedName("name")
    private String name;

    @SerializedName("caption")
    private String caption;

    @SerializedName("attachments")
    private FBAttachments attachments;

    @SerializedName("from")
    private FBUser from;

    @SerializedName("type")
    private String type;

    @SerializedName("updated_time")
    private String updated_time;

    @SerializedName("comments")
    private FBComments comments;

    public String getId() {
        return postId;
    }

    public void setId(String id) {
        this.postId = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public FBAttachments getFBAttachments() {
        return attachments;
    }

    public void setFBAttachments(FBAttachments attachments) {
        this.attachments = attachments;
    }

    public FBUser getFBUser() {
        return from;
    }

    public void setFBUser(FBUser from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public FBComments getFBComments() {
        return comments;
    }

    public void setFBComments(FBComments comments) {
        this.comments = comments;
    }

}