package slidenerd.vivz.fpam.model.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.realm.Dailytics;

/**
 * Created by vivz on 21/09/15.
 */
public class DailyticsParcelConverter extends RealmListParcelConverter<Dailytics> {
    @Override
    public void itemToParcel(Dailytics input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Dailytics.class, input), 0);
    }

    @Override
    public Dailytics itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Dailytics.class.getClassLoader()));
    }
}
