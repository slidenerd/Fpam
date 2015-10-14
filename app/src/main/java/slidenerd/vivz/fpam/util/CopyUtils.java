package slidenerd.vivz.fpam.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.GroupMetaData;

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
        Admin admin = new Admin(src.getId(), src.getEmail(), src.getName(), src.getWidth(), src.getHeight(), src.isSilhouette(), src.getUrl());
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

    public static ArrayList<GroupMetaData> duplicateGroupMetas(RealmResults<GroupMetaData> results) {
        ArrayList<GroupMetaData> groupMetaDatas = new ArrayList<>();
        for (GroupMetaData realmGroupMetaData : results) {
            GroupMetaData groupMetaData = duplicateGroupMeta(realmGroupMetaData);
            groupMetaDatas.add(groupMetaData);
        }
        return groupMetaDatas;
    }

    public static GroupMetaData duplicateGroupMeta(@NonNull GroupMetaData realmGroupMetaData) {
        GroupMetaData groupMetaData = new GroupMetaData();
        groupMetaData.setGroupId(realmGroupMetaData.getGroupId());
        groupMetaData.setTimestamp(realmGroupMetaData.getTimestamp());
        return groupMetaData;
    }
}
