package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.CommentsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.parcel.CommentsParcelConverter;

@Parcel(implementations = {CommentsRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Comments.class})
public class Comments extends RealmObject {
    @PrimaryKey
    private String postId;
    private RealmList<Comment> data = new RealmList<>();
    private String before;
    private String after;

    public Comments() {

    }

    /**
     * @return The data
     */
    public RealmList<Comment> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    @ParcelPropertyConverter(CommentsParcelConverter.class)
    public void setData(RealmList<Comment> data) {
        this.data = data;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}