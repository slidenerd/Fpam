package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.AttachmentRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {AttachmentRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Attachment.class})
public class Attachment extends RealmObject {


    private AttachmentMedia media;
    private String type;
    private String url;

    public Attachment() {

    }

    public Attachment(String url, String type, AttachmentMedia media) {
        this.media = media;
        this.type = type;
        this.url = url;
    }


    /**
     * @return The media
     */
    public AttachmentMedia getMedia() {
        return media;
    }

    /**
     * @param media The media
     */
    public void setMedia(AttachmentMedia media) {
        this.media = media;
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
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}