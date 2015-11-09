package slidenerd.vivz.fpam.util;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 06/10/15.
 */
public class ModelUtils {

    public static String getUserGroupCompositePrimaryKey(String userId, String groupId) {
        return userId + ":" + groupId;
    }

}
