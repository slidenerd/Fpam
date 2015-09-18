package slidenerd.vivz.fpam.util;

import java.util.ArrayList;
import java.util.Collections;
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
import slidenerd.vivz.fpam.model.realm.RealmGroup;
import slidenerd.vivz.fpam.model.realm.RealmPost;
import slidenerd.vivz.fpam.model.realm.RealmUser;

/**
 * Created by vivz on 18/09/15.
 */
public class CopyUtils {
    public static synchronized final RealmAdmin createFrom(Admin admin) {
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

    public static synchronized final Admin createFrom(RealmAdmin realmAdmin) {
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

    public static synchronized final RealmGroup createFrom(Group group) {
        RealmGroup realmGroup = new RealmGroup();
        realmGroup.setId(group.getId());
        realmGroup.setName(group.getName());
        realmGroup.setIcon(group.getIcon());
        realmGroup.setUnread(group.getUnread());
        return realmGroup;
    }

    public static synchronized final ArrayList<RealmGroup> createFromGroups(List<Group> listGroups) {
        ArrayList<RealmGroup> listRealmGroups = new ArrayList<>(listGroups.size());
        for (Group group : listGroups) {
            RealmGroup realmGroup = createFrom(group);
            listRealmGroups.add(realmGroup);
        }
        return listRealmGroups;
    }

    public static synchronized final RealmUser createFrom(User user) {
        RealmUser realmUser = new RealmUser();
        realmUser.setId(user.getId());
        realmUser.setName(user.getName());
        return realmUser;
    }

    public static synchronized final RealmComment createFrom(Comment comment) {
        RealmComment realmComment = new RealmComment();
        realmComment.setId(comment.getId());
        realmComment.setMessage(comment.getMessage());
        realmComment.setCreatedTime(comment.getCreatedTime());
        realmComment.setFrom(createFrom(comment.getFrom()));
        return realmComment;
    }

    public static synchronized final RealmList<RealmComment> createFromComments(List<Comment> listComments) {
        RealmList<RealmComment> listRealmComments = new RealmList<>();
        for (Comment comment : listComments) {
            RealmComment realmComment = createFrom(comment);
            listRealmComments.add(realmComment);
        }
        return listRealmComments;
    }

    public static synchronized final RealmAttachment createFrom(Attachment attachment) {
        RealmAttachment realmAttachment = new RealmAttachment();
        realmAttachment.setType(attachment.getType());
        realmAttachment.setUrl(attachment.getUrl());
        AttachmentImage image = null;
        if (attachment.getAttachmentMedia() != null && (image = attachment.getAttachmentMedia().getImage()) != null) {
            realmAttachment.setWidth(image.getWidth());
            realmAttachment.setHeight(image.getHeight());
            realmAttachment.setSrc(image.getSrc());
        }
        return realmAttachment;
    }

    public static synchronized final RealmList<RealmAttachment> createFromAttachments(List<Attachment> listAttachments) {
        RealmList<RealmAttachment> listRealmAttachments = new RealmList<>();
        for (Attachment attachment : listAttachments) {
            RealmAttachment realmAttachment = createFrom(attachment);
            listRealmAttachments.add(realmAttachment);
        }
        return listRealmAttachments;
    }

    public static synchronized final RealmPost createFrom(Group group, Post post) {
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
        User from = null;
        if ((from = post.getFrom()) != null) {
            RealmUser realmUser = createFrom(from);
            realmPost.setFrom(realmUser);
        } else {
            L.m("Did not find the person posting the post for " + post.getId());
        }
        List<Comment> listComments = Collections.emptyList();
        if (post.getComments() != null && (listComments = post.getComments().getData()) != null) {
            RealmList<RealmComment> listRealmComments = createFromComments(listComments);
            realmPost.setComments(listRealmComments);
        }
        List<Attachment> listAttachments = Collections.emptyList();
        if (post.getAttachments() != null && (listAttachments = post.getAttachments().getData()) != null) {
            RealmList<RealmAttachment> listRealmAttachments = createFromAttachments(listAttachments);
            realmPost.setAttachments(listRealmAttachments);
        }
        return realmPost;
    }

    public static synchronized final RealmList<RealmPost> createFromPosts(Group group, List<Post> listPosts) {
        RealmList<RealmPost> listRealmPosts = new RealmList<>();
        for (Post post : listPosts) {
            RealmPost realmPost = createFrom(group, post);
            listRealmPosts.add(realmPost);
        }
        return listRealmPosts;
    }

}
