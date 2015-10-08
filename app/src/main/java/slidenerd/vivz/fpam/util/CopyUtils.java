package slidenerd.vivz.fpam.util;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;

public class CopyUtils {
    public static ArrayList<Group> duplicateGroups(RealmResults<Group> listSourceGroups) {
        ArrayList<Group> listDestinationGroups = new ArrayList<>();
        for (Group sourceGroup : listSourceGroups) {
            Group destinationGroup = new Group(sourceGroup.getId(), sourceGroup.getName(), sourceGroup.getIcon(), sourceGroup.getUnread());
            listDestinationGroups.add(destinationGroup);
        }
        return listDestinationGroups;
    }

    public static Admin duplicateAdmin(Admin src) {
        Admin admin = new Admin(src.getId(), src.getEmail(), src.getFirstName(), src.getLastName(), src.getWidth(), src.getHeight(), src.isSilhouette(), src.getUrl());
        return admin;
    }

    public static ArrayList<Post> duplicatePosts(RealmResults<Post> realmPosts) {
        ArrayList<Post> listPosts = new ArrayList<>();
//        for (Post realmPost : realmPosts) {
//            Post post = new Post(realmPost.getPostId(), realmPost.getFrom(), realmPost.getMessage(), realmPost.getName(), realmPost.getCaption(), realmPost.getDescription(), realmPost.getLink(), realmPost.getPicture(), realmPost.getType(), realmPost.getAttachments(), realmPost.getComments(), realmPost.getUpdatedTime());
//            listPosts.add(post);
//        }
        return listPosts;
    }

    public static Post duplicatePost(Post realmPost) {
        Post post = new Post();
        post.setPostId(realmPost.getPostId());
        post.setUserId(realmPost.getUserId());
        post.setUserName(realmPost.getUserName());
        post.setMessage(realmPost.getMessage());
        post.setType(realmPost.getType());
        post.setCreatedTime(realmPost.getCreatedTime());
        post.setUpdatedTime(realmPost.getUpdatedTime());
        post.setName(realmPost.getName());
        post.setCaption(realmPost.getCaption());
        post.setDescription(realmPost.getDescription());
        post.setPicture(realmPost.getPicture());
        post.setLink(realmPost.getLink());
        return post;
    }
}
