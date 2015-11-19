package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 17/11/15.
 */
public class TopKeywords extends RealmObject {
    @PrimaryKey
    private String compositeGroupKeywordId;
    private int count;

    public static String computeGroupKeywordId(String groupId, String keyword) {
        return groupId + ":" + keyword;
    }

    public static String getGroupId(String compositeGroupKeywordId) {
        return compositeGroupKeywordId.substring(0, compositeGroupKeywordId.indexOf(':'));
    }

    public static String getKeyword(String compositeGroupKeywordId) {
        return compositeGroupKeywordId.substring(compositeGroupKeywordId.indexOf(':') + 1);
    }

    public String getCompositeGroupKeywordId() {
        return compositeGroupKeywordId;
    }

    public void setCompositeGroupKeywordId(String compositeGroupKeywordId) {
        this.compositeGroupKeywordId = compositeGroupKeywordId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
