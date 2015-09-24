package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {Post.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Post.class})
public class Post extends RealmObject {

    private User from;

    private String message;

    private String caption;

    private String description;

    private String name;

    private String picture;

    private String type;

    private String updated_time;

    private String link;

    @PrimaryKey
    private String id;

    private Comments comments;


    private Attachments attachments;

    public Post() {

    }

    public Post(String id, User from, String message, String name, String caption, String description, String link, String picture, String type, Attachments attachments, Comments comments, String updated_time) {
        this.from = from;
        this.message = message;
        this.caption = caption;
        this.description = description;
        this.name = name;
        this.picture = picture;
        this.type = type;
        this.updated_time = updated_time;
        this.link = link;
        this.id = id;
        this.comments = comments;
        this.attachments = attachments;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}