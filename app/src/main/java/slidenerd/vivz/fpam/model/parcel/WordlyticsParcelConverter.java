package slidenerd.vivz.fpam.model.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.realm.Wordlytics;

/**
 * Created by vivz on 21/09/15.
 */
public class WordlyticsParcelConverter extends RealmListParcelConverter<Wordlytics> {
    @Override
    public void itemToParcel(Wordlytics input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Wordlytics.class, input), 0);
    }

    @Override
    public Wordlytics itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Wordlytics.class.getClassLoader()));
    }
}
