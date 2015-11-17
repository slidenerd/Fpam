package slidenerd.vivz.fpam.model.realm;

import io.realm.RealmObject;

/**
 * Created by vivz on 17/11/15.
 */
public class Occurrence extends RealmObject {
    private String text;
    private int count;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
