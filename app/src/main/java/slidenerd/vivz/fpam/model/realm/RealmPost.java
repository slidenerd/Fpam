package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 17/09/15.
 */
public class RealmPost extends RealmObject {

    private String groupId;

    private RealmUser from;

    private String message;

    private String caption;

    private String description;

    private String name;

    private String picture;

    private String type;

    private String updatedTime;

    private String link;

    @PrimaryKey
    private String id;

    private RealmList<RealmComment> comments = new RealmList<>();

    private RealmList<RealmAttachment> attachments = new RealmList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<RealmComment> getComments() {
        return comments;
    }

    public void setComments(RealmList<RealmComment> comments) {
        this.comments = comments;
    }

    public RealmList<RealmAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(RealmList<RealmAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
