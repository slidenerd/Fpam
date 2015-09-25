package slidenerd.vivz.fpam.model.json.group;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.GroupsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.parcel.GroupsParcelConverter;

/**
 * Created by vivz on 25/09/15.
 */
@Parcel(implementations = {GroupsRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Groups.class})
public class Groups extends RealmObject {
    @PrimaryKey
    private long timestamp;
    private RealmList<Group> groups = new RealmList<>();
    private String before;
    private String after;
    private String previous;
    private String next;

    public Groups() {

    }

    public Groups(long timestamp, RealmList<Group> groups, String before, String after, String previous, String next) {
        this.timestamp = timestamp;
        this.groups = groups;
        this.before = before;
        this.after = after;
        this.previous = previous;
        this.next = next;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Group> getGroups() {
        return groups;
    }

    @ParcelPropertyConverter(GroupsParcelConverter.class)
    public void setGroups(RealmList<Group> groups) {
        this.groups = groups;
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

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
