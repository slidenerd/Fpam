package slidenerd.vivz.fpam.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.realm.GroupMeta;
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
