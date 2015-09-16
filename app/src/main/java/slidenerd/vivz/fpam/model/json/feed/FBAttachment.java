package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FBAttachment implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("media")
    private FBAttachmentMedia media;

    @SerializedName("url")
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FBAttachmentMedia getFBAttachmentMedia() {
        return media;
    }

    public void setFBAttachmentMedia(FBAttachmentMedia media) {
        this.media = media;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}