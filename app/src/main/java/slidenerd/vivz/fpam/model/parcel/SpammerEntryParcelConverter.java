package slidenerd.vivz.fpam.model.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.realm.SpammerEntry;

/**
 * Created by vivz on 21/09/15.
 */
public class SpammerEntryParcelConverter extends RealmListParcelConverter<SpammerEntry> {
    @Override
    public void itemToParcel(SpammerEntry input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(SpammerEntry.class, input), 0);
    }

    @Override
    public SpammerEntry itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(SpammerEntry.class.getClassLoader()));
    }
}
