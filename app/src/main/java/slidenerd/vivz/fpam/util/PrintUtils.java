package slidenerd.vivz.fpam.util;

import java.util.List;

import slidenerd.vivz.fpam.model.json.feed.Attachment;
import slidenerd.vivz.fpam.model.json.feed.AttachmentImage;
import slidenerd.vivz.fpam.model.json.feed.AttachmentMedia;
import slidenerd.vivz.fpam.model.json.feed.Attachments;
import slidenerd.vivz.fpam.model.json.feed.Comment;
import slidenerd.vivz.fpam.model.json.feed.Comments;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.feed.User;

public class PrintUtils {
    public static String toString(User user) {
        return "\n" + user.getId()
                + "\n" + user.getName()
                + "\n";
    }

    public static String toString(Comment comment) {
        return "\n" + comment.getId()
                + "\n" + comment.getMessage()
                + toString(comment.getFrom());
    }

    public static String toString(Comments comments) {
        StringBuilder stringBuilder = new StringBuilder();
        if (comments != null) {
            for (Comment comment : comments.getData()) {
                stringBuilder.append("\n").append(toString(comment)).append("\n");
            }
        } else {
            stringBuilder.append("\nDid not find any comments\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(AttachmentImage attachmentImage) {
        return "\n" + attachmentImage.getWidth()
                + "\n" + attachmentImage.getHeight()
                + "\n" + attachmentImage.getSrc() + "\n";
    }

    public static String toString(AttachmentMedia attachmentMedia) {
        return "\n" + (attachmentMedia == null ? "\nAttachment Media with no image" : toString(attachmentMedia.getImage()));
    }

    public static String toString(Attachment attachment) {
        return "\n" + attachment.getType()
                + "\n" + attachment.getUrl()
                + attachment.getMedia() == null ? "\nAttachment with no media" : toString(attachment.getMedia());
    }

    public static String toString(Attachments attachments) {
        StringBuilder stringBuilder = new StringBuilder();
        if (attachments != null) {
            for (Attachment attachment : attachments.getData()) {
                stringBuilder.append("\n").append(toString(attachment)).append("\n");
            }
        } else {
            stringBuilder.append("\nDid not find any attachments\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(Post post) {
        return "\n" + post.getId()
                + "\n" + post.getName()
                + "\n" + post.getMessage()
                + "\n" + post.getType()
                + "\n" + post.getCaption()
                + "\n" + post.getDescription()
                + "\n" + post.getPicture()
                + "\n" + post.getUpdated_time()
                + "\n" + toString(post.getFrom())
                + "\n" + toString(post.getAttachments())
                + "\n" + toString(post.getComments()) + "\n";
    }

    public static String toString(List<Post> listPosts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Post post : listPosts) {
            stringBuilder.append("\n").append(toString(post)).append("\n");
        }
        return stringBuilder.toString();
    }

}
