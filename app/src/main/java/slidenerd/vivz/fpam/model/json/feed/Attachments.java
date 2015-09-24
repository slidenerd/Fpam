package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.AttachmentsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import slidenerd.vivz.fpam.parcel.AttachmentsParcelConverter;

@Parcel(implementations = {AttachmentsRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Attachments.class})
public class Attachments extends RealmObject {

    private RealmList<Attachment> data = new RealmList<>();

    public Attachments() {

    }

    public Attachments(RealmList<Attachment> data) {
        if (data == null) {
            this.data = new RealmList<>();
        } else {
            this.data = data;
        }
    }

    /**
     * @return The data
     */
    public RealmList<Attachment> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    @ParcelPropertyConverter(AttachmentsParcelConverter.class)
    public void setData(RealmList<Attachment> data) {
        this.data = data;
    }

}