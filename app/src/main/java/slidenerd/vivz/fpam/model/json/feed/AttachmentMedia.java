package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.AttachmentMediaRealmProxy;
import io.realm.RealmObject;


@Parcel(implementations = {AttachmentMediaRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {AttachmentMedia.class})
public class AttachmentMedia extends RealmObject {

    private AttachmentImage image;

    public AttachmentMedia() {

    }

    public AttachmentMedia(AttachmentImage image) {
        this.image = image;
    }

    /**
     * @return The image
     */
    public AttachmentImage getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(AttachmentImage image) {
        this.image = image;
    }

}