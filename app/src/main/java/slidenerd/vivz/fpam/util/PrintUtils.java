package slidenerd.vivz.fpam.util;

import java.util.List;

import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;

public class PrintUtils {

    public static String toString(Post post) {
        if (post == null) return "\n";
        return "\n" + post.getPostId()
                + "\n" + post.getUserId()
                + "\n" + post.getUserName()
                + "\n" + post.getName()
                + "\n" + post.getMessage()
                + "\n" + post.getType()
                + "\n" + post.getCaption()
                + "\n" + post.getDescription()
                + "\n" + post.getPicture()
                + "\n" + post.getLink()
                + "\n" + post.getCreatedTime()
                + "\n" + post.getUpdatedTime()
                + "\n" + post.getType()
                + "\n";
    }

    public static String toString(List<Post> listPosts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Post post : listPosts) {
            stringBuilder.append("\n")
                    .append("id " + post.getPostId())
                    .append("name " + post.getUserName())
                    .append("message " + post.getMessage())
                    .append(toString(post)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(Spammer spammer) {
        if (spammer == null) return "\n";
        return "\n" + spammer.getUserGroupCompositeId()
                + "\n" + spammer.getUserName()
                + "\n" + spammer.getSpamCount()
                + "\n" + spammer.getTimestamp()
                + "\n" + spammer.isAllowed();
    }

    public static String toString(Admin admin) {
        if (admin == null) return "\n";
        return "\n" + admin.getId()
                + "\n" + admin.getName()
                + "\n" + admin.getEmail()
                + "\n" + admin.getWidth()
                + "\n" + admin.getHeight()
                + "\n" + admin.isSilhouette()
                + "\n" + admin.getUrl();
    }

    public static String toString(Group group) {
        if (group == null) return "";
        return "\n" + group.getId()
                + "\n" + group.getName()
                + "\n" + group.getIcon()
                + "\n" + group.getUnread()
                + "\n" + group.getTimestamp()
                + "\n" + group.isMonitored();
    }
}
