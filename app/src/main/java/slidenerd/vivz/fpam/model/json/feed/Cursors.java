package slidenerd.vivz.fpam.model.json.feed;


import org.parceler.Parcel;

import io.realm.RealmObject;

@Parcel(implementations = {Cursors.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Cursors.class})
public class Cursors extends RealmObject {


    private String after;

    private String before;

    public Cursors() {

    }

    public Cursors(String before, String after) {

    }

    /**
     * @return The after
     */
    public String getAfter() {
        return after;
    }

    /**
     * @param after The after
     */
    public void setAfter(String after) {
        this.after = after;
    }

    /**
     * @return The before
     */
    public String getBefore() {
        return before;
    }

    /**
     * @param before The before
     */
    public void setBefore(String before) {
        this.before = before;
    }

}
