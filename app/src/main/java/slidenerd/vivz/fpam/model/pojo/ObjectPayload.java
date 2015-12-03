package slidenerd.vivz.fpam.model.pojo;

import com.facebook.FacebookRequestError;

/**
 * Created by vivz on 03/12/15.
 */
public class ObjectPayload<T> {
    public T data;
    public FacebookRequestError error;
}
