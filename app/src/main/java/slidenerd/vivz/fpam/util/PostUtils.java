package slidenerd.vivz.fpam.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 06/10/15.
 */
public class PostUtils {
    public static String calculatePostingFrequency(ArrayList<Post> listSortedByTimePosts) {
        int numberOfScannedPosts = 0;
        long averageInterval = 0;
        long sumOfIntervals = 0;
        long difference = 0;
        long currentCreatedTime = 0;
        long prevCreatedTime = 0;

        for (Post post : listSortedByTimePosts) {
            currentCreatedTime = post.getCreatedTime();
            if (currentCreatedTime == Constants.NA)
                continue;
            difference = Math.abs(currentCreatedTime - prevCreatedTime);
            if (numberOfScannedPosts > 0) {
                sumOfIntervals += difference;
            }
            prevCreatedTime = currentCreatedTime;
            numberOfScannedPosts++;
        }
        if (numberOfScannedPosts > 0) {
            averageInterval = sumOfIntervals / numberOfScannedPosts;
        }
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(averageInterval),
                TimeUnit.MILLISECONDS.toMinutes(averageInterval) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(averageInterval)),
                TimeUnit.MILLISECONDS.toSeconds(averageInterval) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(averageInterval)));
    }

    public static void sortByCreatedTime(ArrayList<Post> listPosts) {
        final int LESSER = -1;
        final int GREATER = 1;
        final int EQUAL = 0;
        Collections.sort(listPosts, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                if (lhs.getCreatedTime() < rhs.getCreatedTime()) {
                    return LESSER;
                } else if (lhs.getCreatedTime() > rhs.getCreatedTime()) {
                    return GREATER;
                } else {
                    return EQUAL;
                }
            }
        });
    }

    public static ArrayList<Post> getPostsLast24Hours(ArrayList<Post> listAllPostsUnsorted) {
        long now = System.currentTimeMillis();
        long aDayAgo = now - (24 * 60 * 60 * 1000);
        ArrayList<Post> listPostsLast24Hours = new ArrayList<>(listAllPostsUnsorted.size());
        for (Post post : listAllPostsUnsorted) {
            if (post.getCreatedTime() > aDayAgo && post.getCreatedTime() < now) {
                listPostsLast24Hours.add(post);
            }
        }
        return listPostsLast24Hours;
    }
}
