package slidenerd.vivz.fpam.model.pojo;

import slidenerd.vivz.fpam.model.realm.Spammer;

/**
 * Created by vivz on 25/11/15.
 */
public class Item {
    public int position;
    public String content;
    public String postId;
    public String userId;
    public String userName;
    public boolean empty;
    public boolean status;
    public boolean bySpammer;
    public boolean byKeyword;
    public Spammer spammer;
}
