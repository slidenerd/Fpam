package slidenerd.vivz.fpam.model.json.admin;

import org.parceler.Parcel;

import io.realm.AdminRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {AdminRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Admin.class})
public class Admin extends RealmObject {

    @PrimaryKey
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private int width;
    private int height;
    private boolean isSilhouette;
    private String url;

    public Admin() {
    }

    public Admin(String id, String email, String firstName, String lastName, int width, int height, boolean isSilhouette, String url) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.width = width;
        this.height = height;
        this.isSilhouette = isSilhouette;
        this.url = url;
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
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isSilhouette() {
        return isSilhouette;
    }

    public void setIsSilhouette(boolean isSilhouette) {
        this.isSilhouette = isSilhouette;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}