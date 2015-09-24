package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {User.class},
        value = Parcel.Serialization.BEAN,
        analyze = {User.class})
public class User extends RealmObject {

    private String name;

    @PrimaryKey
    private String id;

    public User() {

    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
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

}