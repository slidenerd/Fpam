package slidenerd.vivz.fpam.model.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.feed.Comment;
import slidenerd.vivz.fpam.model.realm.Spammer;

/**
 * Created by vivz on 21/09/15.
 */
public class SpammerParcelConverter extends RealmListParcelConverter<Spammer> {
    @Override
    public void itemToParcel(Spammer input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Spammer.class, input), 0);
    }

    @Override
    public Spammer itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Spammer.class.getClassLoader()));
    }
}
