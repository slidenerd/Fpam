package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 11/11/15.
 */
public class Dailytics extends RealmObject {
    //A combination of group id followed by : followed by date in dd-MM-yyyy format
    @PrimaryKey
    private String compositeGroupDateId;

    //Total number of posts scanned on a daily basis
    private int scanned;

    //Total number of posts deleted on a daily basis
    private int deleted;

    //Total number of posts deleted because they had an empty message on a daily basis
    private int deletedEmpty;

    //Total number of posts deleted because they had spam keywords in them on a daily basis
    private int deletedKeywords;

    //Total number of posts deleted because they were made by a Spammer on a daily basis
    private int deletedSpammer;

    //Total number of posts whose delete failed while executing on Graph API on a daily basis
    private int failed;

    public Dailytics(String compositeGroupDateId, int scanned, int deleted, int deletedEmpty, int deletedKeywords, int deletedSpammer, int failed) {
        this.compositeGroupDateId = compositeGroupDateId;
        this.scanned = scanned;
        this.deleted = deleted;
        this.deletedEmpty = deletedEmpty;
        this.deletedKeywords = deletedKeywords;
        this.deletedSpammer = deletedSpammer;
        this.failed = failed;
    }

    //Must have default constructor if a custom constructor is included
    public Dailytics() {

    }

    public static String toString(Dailytics dailytics) {
        return "Dailytics{" +
                "compositeGroupDateId='" + dailytics.compositeGroupDateId + '\'' +
                ", scanned=" + dailytics.scanned +
                ", deleted=" + dailytics.deleted +
                ", deletedEmpty=" + dailytics.deletedEmpty +
                ", deletedKeywords=" + dailytics.deletedKeywords +
                ", deletedSpammer=" + dailytics.deletedSpammer +
                ", failed=" + dailytics.failed +
                '}';
    }

    public String getCompositeGroupDateId() {
        return compositeGroupDateId;
    }

    public void setCompositeGroupDateId(String compositeGroupDateId) {
        this.compositeGroupDateId = compositeGroupDateId;
    }

    public int getScanned() {
        return scanned;
    }

    public void setScanned(int scanned) {
        this.scanned = scanned;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getDeletedEmpty() {
        return deletedEmpty;
    }

    public void setDeletedEmpty(int deletedEmpty) {
        this.deletedEmpty = deletedEmpty;
    }

    public int getDeletedKeywords() {
        return deletedKeywords;
    }

    public void setDeletedKeywords(int deletedKeywords) {
        this.deletedKeywords = deletedKeywords;
    }

    public int getDeletedSpammer() {
        return deletedSpammer;
    }

    public void setDeletedSpammer(int deletedSpammer) {
        this.deletedSpammer = deletedSpammer;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }
}
