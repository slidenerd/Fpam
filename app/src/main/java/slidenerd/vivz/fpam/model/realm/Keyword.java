package slidenerd.vivz.fpam.model.realm;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.model.json.Group;

/**
 * Created by vivz on 02/11/15.
 */
public class Keyword extends RealmObject {
    @PrimaryKey
    private String keyword;
    private long timestamp;
    private String groups;

    //Must have default constructor if a custom constructor is included
    public Keyword() {

    }

    public Keyword(String keyword, long timestamp, String groups) {
        this.keyword = keyword;
        this.timestamp = timestamp;
        this.groups = groups;
    }

    public static String toPrint(List<Keyword> keywords) {
        StringBuffer buffer = new StringBuffer();
        for (Keyword keyword : keywords) {
            buffer.append(keyword.getKeyword()).append("\n").append(keyword.getTimestamp()).append("\n").append(keyword.getGroups()).append("\n");
        }
        return buffer.toString();
    }

    public static String toPrint(Keyword keyword) {
        return "Keyword{" +
                "keyword='" + keyword.keyword + '\'' +
                ", timestamp=" + keyword.timestamp +
                ", groups=" + keyword.groups +
                '}';
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}