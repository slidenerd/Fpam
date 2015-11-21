package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 17/11/15.
 */
public class TopKeyword extends RealmObject {
    @PrimaryKey
    private String compositeGroupOrderId;
    private String keyword;
    private int count;

    public static String computeGroupKeywordId(String groupId, int order) {
        return groupId + ":" + order;
    }

    public static String getGroupId(String compositeGroupKeywordId) {
        return compositeGroupKeywordId.substring(0, compositeGroupKeywordId.indexOf(':'));
    }

    public static int getOrder(String compositeGroupKeywordId) {
        return Integer.parseInt(compositeGroupKeywordId.substring(compositeGroupKeywordId.indexOf(':') + 1));
    }

    public String getCompositeGroupOrderId() {
        return compositeGroupOrderId;
    }

    public void setCompositeGroupOrderId(String compositeGroupOrderId) {
        this.compositeGroupOrderId = compositeGroupOrderId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
