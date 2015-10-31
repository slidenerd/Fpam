package slidenerd.vivz.fpam.util;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 06/10/15.
 */
public class ModelUtils {
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

    public static String getUserGroupCompositePrimaryKey(String userId, String groupId) {
        return userId + ":" + groupId;
    }

    public static long getPostItemId(String postId) {
        long postItemId = 0;
        StringBuffer buffer = new StringBuffer(postId.length() / 2);
        int index = postId.indexOf('_');
        if (index == -1) {
            postItemId = RecyclerView.NO_ID;
        } else {
            String suffix = postId.substring(index + 1, postId.length());
            if (postId.length() == index + 1) {
                postItemId = RecyclerView.NO_ID;
            } else {
                for (char c : suffix.toCharArray()) {
                    if (Character.isDigit(c)) {
                        buffer.append(c);
                    }
                }
                try {
                    postItemId = Long.parseLong(buffer.toString());
                } catch (NumberFormatException e) {
                    postItemId = RecyclerView.NO_ID;
                }
            }
        }
        return postItemId;
    }
}
