package slidenerd.vivz.fpam.util;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 06/10/15.
 */
public class PostUtils {
    public static long calculateAverageInterval(ArrayList<Post> posts) {
        long averageInterval = 0;
        for (int i = 0; i < posts.size() - 1; i++) {
            Post current = posts.get(i);
            Post next = posts.get(i + 1);
            averageInterval += Math.abs(next.getUpdatedTime() - current.getUpdatedTime());
            L.m("average interval " + averageInterval);
        }
        if (posts.size() - 1 > 0) {
            averageInterval /= (posts.size() - 1);
        }
        return averageInterval;
    }
}
