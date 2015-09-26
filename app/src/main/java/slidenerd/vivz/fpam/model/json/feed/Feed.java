package slidenerd.vivz.fpam.model.json.feed;

import org.parceler.Parcel;

import io.realm.FeedRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 25/09/15.
 */
@Parcel(implementations = {Feed.class},
        value = Parcel.Serialization.BEAN,
        analyze = {FeedRealmProxy.class})
public class Feed extends RealmObject {
    @PrimaryKey
    private String groupId;
    private long timestamp;
    private RealmList<Post> listPosts = new RealmList<>();
    private String previous;
    private String next;

    public Feed() {

    }

    public Feed(String groupId, long timestamp, RealmList<Post> listPosts, String previous, String next) {
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.listPosts = listPosts;
        this.previous = previous;
        this.next = next;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Post> getListPosts() {
        return listPosts;
    }

    public void setListPosts(RealmList<Post> listPosts) {
        this.listPosts = listPosts;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}