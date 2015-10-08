package slidenerd.vivz.fpam.util;

import java.util.List;

import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.realm.Spammer;

public class PrintUtils {

    public static String toString(Post post) {
        return "\n" + post.getPostId()
                + "\n" + post.getName()
                + "\n" + post.getMessage()
                + "\n" + post.getType()
                + "\n" + post.getCaption()
                + "\n" + post.getDescription()
                + "\n" + post.getPicture()
                + "\n" + post.getUpdatedTime();
    }

    public static String toString(List<Post> listPosts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Post post : listPosts) {
            stringBuilder.append("\n").append(toString(post)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(Spammer spammer) {
        return "\n" + spammer.getUserGroupCompositeId()
                + "\n" + spammer.getUserName()
                + "\n" + spammer.getSpamCount()
                + "\n" + spammer.getTimestamp();
    }

}
