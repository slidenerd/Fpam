package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.AttachmentsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.parcel.AttachmentsParcelConverter;

@Parcel(implementations = {AttachmentsRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Attachments.class})
public class Attachments extends RealmObject {

    @PrimaryKey
    private String postId;
    private RealmList<Attachment> data = new RealmList<>();

    public Attachments() {

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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}