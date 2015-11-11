package slidenerd.vivz.fpam.model.realm;

import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import slidenerd.vivz.fpam.model.parcel.DailyticsParcelConverter;
import slidenerd.vivz.fpam.model.parcel.SpammerParcelConverter;
import slidenerd.vivz.fpam.model.parcel.WordlyticsParcelConverter;

/**
 * Created by vivz on 10/11/15.
 */
public class Analytics extends RealmObject {
    @PrimaryKey
    private String groupId;
    private String groupName;
    private RealmList<Dailytics> entries = new RealmList<>();
    private RealmList<Wordlytics> keywords = new RealmList<>();
    //Store the top X spammers of this groupId
    private RealmList<Spammer> spammers = new RealmList<>();

    //Must have default constructor if a custom constructor is included
    public Analytics() {

    }

    public Analytics(String groupId, String groupName, RealmList<Dailytics> entries, RealmList<Wordlytics> keywords, RealmList<Spammer> spammers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.entries = (entries != null) ? entries : new RealmList<Dailytics>();
        this.keywords = (keywords != null) ? keywords : new RealmList<Wordlytics>();
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


    @ParcelPropertyConverter(SpammerParcelConverter.class)
    public void setSpammers(RealmList<Spammer> spammers) {
        this.spammers = spammers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public RealmList<Wordlytics> getKeywords() {
        return keywords;
    }

    @ParcelPropertyConverter(WordlyticsParcelConverter.class)
    public void setKeywords(RealmList<Wordlytics> keywords) {
        this.keywords = keywords;
    }

    public RealmList<Dailytics> getEntries() {
        return entries;
    }

    @ParcelPropertyConverter(DailyticsParcelConverter.class)
    public void setEntries(RealmList<Dailytics> entries) {
        this.entries = entries;
    }
}
