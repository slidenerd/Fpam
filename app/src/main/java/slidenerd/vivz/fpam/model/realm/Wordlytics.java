package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 11/11/15.
 */
public class Wordlytics extends RealmObject {
    //The combination of group id followed by : followed by the keyword under consideration
    @PrimaryKey
    private String compositeGroupKeywordId;
    private int count;

    //Must have default constructor if a custom constructor is included
    public Wordlytics() {

    }

    public Wordlytics(String compositeGroupKeywordId, int count) {
        this.compositeGroupKeywordId = compositeGroupKeywordId;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCompositeGroupKeywordId() {
        return compositeGroupKeywordId;
    }

    public void setCompositeGroupKeywordId(String compositeGroupKeywordId) {
        this.compositeGroupKeywordId = compositeGroupKeywordId;
    }
}
