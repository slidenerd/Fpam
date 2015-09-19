package slidenerd.vivz.fpam.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.group.Group;

// Specific class for a RealmList<Bar> field
public class GroupListParcelConverter extends RealmListParcelConverter<Group> {

    @Override
    public void itemToParcel(Group input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Group itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Group.class.getClassLoader()));
    }
}