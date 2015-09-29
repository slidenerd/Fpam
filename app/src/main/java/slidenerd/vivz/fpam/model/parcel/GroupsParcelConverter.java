package slidenerd.vivz.fpam.model.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 21/09/15.
 */
public class GroupsParcelConverter extends RealmListParcelConverter<Group> {
    @Override
    public void itemToParcel(Group input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Group.class, input), 0);
    }

    @Override
    public Group itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Group.class.getClassLoader()));
    }
}
