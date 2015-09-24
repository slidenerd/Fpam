package slidenerd.vivz.fpam.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.feed.Attachment;

/**
 * Created by vivz on 21/09/15.
 */
public class AttachmentsParcelConverter extends RealmListParcelConverter<Attachment> {
    @Override
    public void itemToParcel(Attachment input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Attachment.class, input), 0);
    }

    @Override
    public Attachment itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Attachment.class.getClassLoader()));
    }
}
