package slidenerd.vivz.fpam.util;

import java.util.Date;
import java.util.List;

import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.realm.GroupMeta;
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
                + "\n" + post.getType();
    }

    public static String toString(List<Post> listPosts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Post post : listPosts) {
            stringBuilder.append("\n").append(toString(post)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(Spammer spammer) {
        if (spammer == null) return "\n";
        return "\n" + spammer.getUserGroupCompositeId()
                + "\n" + spammer.getUserName()
                + "\n" + spammer.getSpamCount()
                + "\n" + spammer.getTimestamp();
    }

    public static String toString(Admin admin) {
        if (admin == null) return "\n";
        return "\n" + admin.getId()
                + "\n" + admin.getFirstName()
                + "\n" + admin.getLastName()
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
                + "\n" + group.getUnread();
    }

    public static String toString(GroupMeta groupMeta) {
        if (groupMeta == null) return "";
        return "\n" + groupMeta.getGroupId()
                + "\n" + groupMeta.getTimestamp()
                + "\n" + new Date(groupMeta.getTimestamp());
    }
}
