package slidenerd.vivz.fpam.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.GroupMeta;

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

    public static Post duplicatePost(Post originalPost) {
        Post post = new Post();
        post.setPostId(originalPost.getPostId());
        post.setUserId(originalPost.getUserId());
        post.setUserName(originalPost.getUserName());
        post.setMessage(originalPost.getMessage());
        post.setType(originalPost.getType());
        post.setCreatedTime(originalPost.getCreatedTime());
        post.setUpdatedTime(originalPost.getUpdatedTime());
        post.setName(originalPost.getName());
        post.setCaption(originalPost.getCaption());
        post.setDescription(originalPost.getDescription());
        post.setPicture(originalPost.getPicture());
        post.setLink(originalPost.getLink());
        return post;
    }

    public static ArrayList<Post> clonePosts(ArrayList<Post> posts) {
        ArrayList<Post> clones = new ArrayList<>(posts.size());
        for (Post post : posts) {
            Post clone = CopyUtils.duplicatePost(post);
            clones.add(clone);
        }
        return clones;
    }

    public static ArrayList<GroupMeta> duplicateGroupMetas(RealmResults<GroupMeta> results) {
        ArrayList<GroupMeta> groupMetas = new ArrayList<>();
        for (GroupMeta realmGroupMeta : results) {
            GroupMeta groupMeta = duplicateGroupMeta(realmGroupMeta);
            groupMetas.add(groupMeta);
        }
        return groupMetas;
    }

    public static GroupMeta duplicateGroupMeta(@NonNull GroupMeta realmGroupMeta) {
        GroupMeta groupMeta = new GroupMeta();
        groupMeta.setGroupId(realmGroupMeta.getGroupId());
        groupMeta.setTimestamp(realmGroupMeta.getTimestamp());
        return groupMeta;
    }
}
