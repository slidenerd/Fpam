package slidenerd.vivz.fpam.model.pojo;

import com.facebook.FacebookRequestError;

import java.util.ArrayList;

/**
 * Created by vivz on 03/12/15.
 */
public class CollectionPayload<T> {
    public ArrayList<T> data = new ArrayList<>();
    public FacebookRequestError error;
}
