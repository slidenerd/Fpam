package slidenerd.vivz.fpam.model.json.admin;

import org.parceler.Parcel;

import io.realm.PictureRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {PictureRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Picture.class})
public class Picture extends RealmObject {

    private PictureData data;

    public Picture() {
    }

    /**
     * @return The data
     */
    public PictureData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(PictureData data) {
        this.data = data;
    }
}