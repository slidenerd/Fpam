package slidenerd.vivz.fpam.model.phrase;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vivz on 04/08/15.
 */
public class Phrase extends RealmObject {
    @PrimaryKey
    private String phrase;
    private long added;


    public Phrase() {
        this(null, 0);
    }

    public Phrase(String phrase, long added) {
        this.phrase = phrase;
        this.added = added;
    }

    public Phrase(String phrase) {
        this(phrase, System.currentTimeMillis());
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }
}
