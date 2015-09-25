package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.AttachmentRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {AttachmentRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Attachment.class})
public class Attachment extends RealmObject {

    private String title;
    private String type;
    private String url;
    private int width;
    private int height;
    private String source;

    public Attachment() {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}