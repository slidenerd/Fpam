package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.CommentsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import slidenerd.vivz.fpam.parcel.CommentsParcelConverter;

@Parcel(implementations = {CommentsRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Comments.class})
public class Comments extends RealmObject {


    private RealmList<Comment> data = new RealmList<>();
    private CommentPaging paging;

    public Comments() {

    }

    public Comments(RealmList<Comment> data, CommentPaging paging) {
        this.data = data;
        this.paging = paging;
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

    /**
     * @return The paging
     */
    public CommentPaging getPaging() {
        return paging;
    }

    /**
     * @param paging The paging
     */
    public void setPaging(CommentPaging paging) {
        this.paging = paging;
    }

}