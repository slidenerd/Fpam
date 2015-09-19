package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.PagingRealmProxy;
import io.realm.RealmObject;


@Parcel(implementations = {PagingRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Paging.class})
public class Paging extends RealmObject {

    @Expose
    private String previous;
    @Expose
    private String next;

    public Paging() {
    }

    /**
     * @return The previous
     */
    public String getPrevious() {
        return previous;
    }

    /**
     * @param previous The previous
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    /**
     * @return The next
     */
    public String getNext() {
        return next;
    }

    /**
     * @param next The next
     */
    public void setNext(String next) {
        this.next = next;
    }
}