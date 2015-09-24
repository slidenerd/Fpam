package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.CommentPagingRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {CommentPagingRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {CommentPaging.class})
public class CommentPaging extends RealmObject {

    private Cursors cursors;

    public CommentPaging() {

    }

    public CommentPaging(Cursors cursors) {
        this.cursors = cursors;
    }

    /**
     * @return The cursors
     */
    public Cursors getCursors() {
        return cursors;
    }

    /**
     * @param cursors The cursors
     */
    public void setCursors(Cursors cursors) {
        this.cursors = cursors;
    }

}