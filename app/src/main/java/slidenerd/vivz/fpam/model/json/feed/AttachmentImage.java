package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.AttachmentImageRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {AttachmentImageRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {AttachmentImage.class})
public class AttachmentImage extends RealmObject {

    private int height;


    private String src;

    private int width;

    public AttachmentImage() {

    }

    public AttachmentImage(String src, int width, int height) {
        this.height = height;
        this.src = src;
        this.width = width;
    }

    /**
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return The src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src The src
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(int width) {
        this.width = width;
    }

}