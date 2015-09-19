package slidenerd.vivz.fpam.util;

import java.util.List;

import io.realm.RealmList;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.admin.Picture;
import slidenerd.vivz.fpam.model.json.admin.PictureData;
import slidenerd.vivz.fpam.model.json.feed.Attachment;
import slidenerd.vivz.fpam.model.json.feed.AttachmentImage;
import slidenerd.vivz.fpam.model.json.feed.Comment;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.feed.User;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.RealmAdmin;
import slidenerd.vivz.fpam.model.realm.RealmAttachment;
import slidenerd.vivz.fpam.model.realm.RealmComment;
import slidenerd.vivz.fpam.model.realm.RealmPost;
import slidenerd.vivz.fpam.model.realm.RealmUser;

public class CopyUtils {
    public static RealmAdmin createFrom(Admin admin) {
        RealmAdmin realmAdmin = new RealmAdmin();
        realmAdmin.setId(admin.getId());
        realmAdmin.setEmail(admin.getEmail());
        realmAdmin.setFirstName(admin.getFirstName());
        realmAdmin.setLastName(admin.getLastName());
        Picture picture = admin.getPicture();
        if (picture != null && picture.getPictureData() != null) {
            PictureData pictureData = picture.getPictureData();
            realmAdmin.setHeight(pictureData.getHeight());
            realmAdmin.setWidth(pictureData.getWidth());
            realmAdmin.setIsSilhouette(pictureData.getIsSilhouette());
            realmAdmin.setUrl(pictureData.getUrl());
        } else {
            L.m("The admin's profile image information was unavailable for saving");
        }
        return realmAdmin;
    }

    public static Admin createFrom(RealmAdmin realmAdmin) {
        Admin admin = new Admin();
        admin.setId(realmAdmin.getId());
        admin.setFirstName(realmAdmin.getFirstName());
        admin.setLastName(realmAdmin.getLastName());
        admin.setEmail(realmAdmin.getEmail());
        PictureData pictureData = new PictureData();
        pictureData.setWidth(realmAdmin.getWidth());
        pictureData.setHeight(realmAdmin.getHeight());
        pictureData.setUrl(realmAdmin.getUrl());
        pictureData.setIsSilhouette(realmAdmin.isSilhouette());
        Picture picture = new Picture();
        picture.setPictureData(pictureData);
        admin.setPicture(picture);
        return admin;
    }


    public static RealmUser createFrom(User user) {
        RealmUser realmUser = new RealmUser();
        realmUser.setId(user.getId());
        realmUser.setName(user.getName());
        return realmUser;
    }

    public static RealmComment createFrom(Comment comment) {
        RealmComment realmComment = new RealmComment();
        realmComment.setId(comment.getId());
        realmComment.setMessage(comment.getMessage());
        realmComment.setCreatedTime(comment.getCreatedTime());
        realmComment.setFrom(createFrom(comment.getFrom()));
        return realmComment;
    }

    public static RealmList<RealmComment> createFromComments(List<Comment> listComments) {
        RealmList<RealmComment> listRealmComments = new RealmList<>();
        for (Comment comment : listComments) {
            RealmComment realmComment = createFrom(comment);
            listRealmComments.add(realmComment);
        }
        return listRealmComments;
    }

    public static RealmAttachment createFrom(Attachment attachment) {
        RealmAttachment realmAttachment = new RealmAttachment();
        realmAttachment.setType(attachment.getType());
        realmAttachment.setUrl(attachment.getUrl());
        AttachmentImage image;
        if (attachment.getAttachmentMedia() != null && (image = attachment.getAttachmentMedia().getImage()) != null) {
            realmAttachment.setWidth(image.getWidth());
            realmAttachment.setHeight(image.getHeight());
            realmAttachment.setSrc(image.getSrc());
        }
        return realmAttachment;
    }

    public static RealmList<RealmAttachment> createFromAttachments(List<Attachment> listAttachments) {
        RealmList<RealmAttachment> listRealmAttachments = new RealmList<>();
        for (Attachment attachment : listAttachments) {
            RealmAttachment realmAttachment = createFrom(attachment);
            listRealmAttachments.add(realmAttachment);
        }
        return listRealmAttachments;
    }

    public static RealmPost createFrom(Group group, Post post) {
        RealmPost realmPost = new RealmPost();
        realmPost.setId(post.getId());
        realmPost.setName(post.getName());
        realmPost.setCaption(post.getCaption());
        realmPost.setDescription(post.getDescription());
        realmPost.setLink(post.getLink());
        realmPost.setType(post.getType());
        realmPost.setUpdatedTime(post.getUpdatedTime());
        realmPost.setMessage(post.getMessage());
        realmPost.setPicture(post.getPicture());
        realmPost.setGroupId(group.getId());
        User from;
        if ((from = post.getFrom()) != null) {
            RealmUser realmUser = createFrom(from);
            realmPost.setFrom(realmUser);
        } else {
            L.m("Did not find the person posting the post for " + post.getId());
        }
        List<Comment> listComments;
        if (post.getComments() != null && (listComments = post.getComments().getData()) != null) {
            RealmList<RealmComment> listRealmComments = createFromComments(listComments);
            realmPost.setComments(listRealmComments);
        }
        List<Attachment> listAttachments;
        if (post.getAttachments() != null && (listAttachments = post.getAttachments().getData()) != null) {
            RealmList<RealmAttachment> listRealmAttachments = createFromAttachments(listAttachments);
            realmPost.setAttachments(listRealmAttachments);
        }
        return realmPost;
    }

    public static RealmList<RealmPost> createFromPosts(Group group, List<Post> listPosts) {
        RealmList<RealmPost> listRealmPosts = new RealmList<>();
        for (Post post : listPosts) {
            RealmPost realmPost = createFrom(group, post);
            listRealmPosts.add(realmPost);
        }
        return listRealmPosts;
    }

}
