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
    private String groupName;
    private RealmList<Postlytics> entries = new RealmList<>();
    private RealmList<Keyword> keywords = new RealmList<>();
    //Store the top X spammers of this groupId
    private RealmList<Spammer> spammers = new RealmList<>();

    //Must have default constructor if a custom constructor is included
    public Analytics() {

    }

    public Analytics(String groupId, String groupName, RealmList<Postlytics> entries, RealmList<Keyword> keywords, RealmList<Spammer> spammers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.entries = (entries != null) ? entries : new RealmList<Postlytics>();
        this.keywords = (keywords != null) ? keywords : new RealmList<Keyword>();
        this.spammers = (spammers != null) ? spammers : new RealmList<Spammer>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public RealmList<Spammer> getSpammers() {
        return spammers;
    }

    public void setSpammers(RealmList<Spammer> spammers) {
        this.spammers = spammers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public RealmList<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(RealmList<Keyword> keywords) {
        this.keywords = keywords;
    }

    public RealmList<Postlytics> getEntries() {
        return entries;
    }

    public void setEntries(RealmList<Postlytics> entries) {
        this.entries = entries;
    }
}
