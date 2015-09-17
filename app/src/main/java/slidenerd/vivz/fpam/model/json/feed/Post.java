package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
    @Expose
    private User from;
    @Expose
    private String message;
    @Expose
    private String caption;
    @Expose
    private String description;
    @Expose
    private String name;
    @Expose
    private String picture;
    @Expose
    private String type;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @Expose
    private String link;
    @Expose
    private String id;
    @Expose
    private Comments comments;
    @Expose
    private Attachments attachments;

    protected Post(Parcel in) {
        from = (User) in.readValue(User.class.getClassLoader());
        message = in.readString();
        caption = in.readString();
        description = in.readString();
        name = in.readString();
        picture = in.readString();
        type = in.readString();
        updatedTime = in.readString();
        link = in.readString();
        id = in.readString();
        comments = (Comments) in.readValue(Comments.class.getClassLoader());
        attachments = (Attachments) in.readValue(Attachments.class.getClassLoader());
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
    public String getUpdatedTime() {
        return updatedTime;
    }

    /**
     * @param updatedTime The updated_time
     */
    public void setUpdatedTime(String updatedTime) {
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
     * @return The comments
     */
    public Comments getComments() {
        return comments;
    }

    /**
     * @param comments The comments
     */
    public void setComments(Comments comments) {
        this.comments = comments;
    }

    /**
     * @return The attachments
     */
    public Attachments getAttachments() {
        return attachments;
    }

    /**
     * @param attachments The attachments
     */
    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(from);
        dest.writeString(message);
        dest.writeString(caption);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(type);
        dest.writeString(updatedTime);
        dest.writeString(link);
        dest.writeString(id);
        dest.writeValue(comments);
        dest.writeValue(attachments);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}