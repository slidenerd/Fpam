package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 10/11/15.
 */
public class Analytics extends RealmObject {

    @PrimaryKey
    private String groupId;
    private RealmList<Frequency> topKeywords = new RealmList<>();
    //Store the top X topSpammers of this groupId
    private RealmList<Spammer> topSpammers = new RealmList<>();

    //Must have default constructor if a custom constructor is included
    public Analytics() {

    }

    public Analytics(String groupId, RealmList<Frequency> topKeywords, RealmList<Spammer> topSpammers) {
        this.groupId = groupId;
        this.topKeywords = topKeywords;
        this.topSpammers = topSpammers;
    }

    public static String toPrint(Analytics analytics) {
        return "Analytics{" +
                "groupId='" + analytics.groupId + '\'' +
                ", topKeywords=" + analytics.topKeywords +
                ", topSpammers=" + analytics.topSpammers +
                '}';
    }

    public RealmList<Spammer> getTopSpammers() {
        return topSpammers;
    }

    public void setTopSpammers(RealmList<Spammer> topSpammers) {
        this.topSpammers = topSpammers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public RealmList<Frequency> getTopKeywords() {
        return topKeywords;
    }

    public void setTopKeywords(RealmList<Frequency> topKeywords) {
        this.topKeywords = topKeywords;
    }
}
