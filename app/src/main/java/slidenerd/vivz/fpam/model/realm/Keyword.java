package slidenerd.vivz.fpam.model.realm;

import org.parceler.ParcelPropertyConverter;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.parcel.KeywordGroupsConverter;

/**
 * Created by vivz on 02/11/15.
 */
public class Keyword extends RealmObject {
    @PrimaryKey
    private String keyword;
    private long timestamp;
    private RealmList<Group> groups = new RealmList<>();

    //Must have default constructor if a custom constructor is included
    public Keyword() {

    }

    public Keyword(String keyword, long timestamp) {
        this(keyword, timestamp, null);
    }

    public Keyword(String keyword, long timestamp, RealmList<Group> groups) {
        this.keyword = keyword;
        this.timestamp = timestamp;
        this.groups = (groups != null ? groups : new RealmList<Group>());
    }

    public static String toString(List<Keyword> keywords) {
        StringBuffer buffer = new StringBuffer();
        for (Keyword keyword : keywords) {
            buffer.append(keyword.getKeyword()).append("\n").append(keyword.getTimestamp()).append("\n");
            List<Group> groups = keyword.getGroups();
            for (Group group : groups) {
                buffer.append(group.getGroupId()).append("\n").append(group.getGroupName()).append("\n");
            }
        }
        return buffer.toString();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public RealmList<Group> getGroups() {
        return groups;
    }

    @ParcelPropertyConverter(KeywordGroupsConverter.class)
    public void setGroups(RealmList<Group> groups) {
        this.groups = groups;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
