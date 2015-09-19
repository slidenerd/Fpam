package slidenerd.vivz.fpam.model.json.admin;

import org.parceler.Parcel;

import io.realm.AdminRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * TODO add support for storing images without crashing the app
 */
@Parcel(implementations = {AdminRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Admin.class})
public class Admin extends RealmObject {

    @PrimaryKey
    private String id;
    private String email;
    private String first_name;
    private String last_name;
//    private Picture picture;

    public Admin() {
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The first_name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * @param first_name The first_name
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * @return The last_name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * @param last_name The last_name
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

//    /**
//     * @return The picture
//     */
//    public Picture getPicture() {
//        return picture;
//    }
//
//    /**
//     * @param picture The picture
//     */
//    public void setPicture(Picture picture) {
//        this.picture = picture;
//    }

}