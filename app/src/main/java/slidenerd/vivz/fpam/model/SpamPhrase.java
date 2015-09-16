package slidenerd.vivz.fpam.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 04/08/15.
 */
public class SpamPhrase extends RealmObject {
    @PrimaryKey
    private String spamPhrase;
    private long added;


    public SpamPhrase() {
        this(null, 0);
    }

    public SpamPhrase(String spamPhrase, long added) {
        this.spamPhrase = spamPhrase;
        this.added = added;
    }

    public SpamPhrase(String spamPhrase) {
        this(spamPhrase, System.currentTimeMillis());
    }

    public String getSpamPhrase() {
        return spamPhrase;
    }

    public void setSpamPhrase(String spamPhrase) {
        this.spamPhrase = spamPhrase;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }
}
