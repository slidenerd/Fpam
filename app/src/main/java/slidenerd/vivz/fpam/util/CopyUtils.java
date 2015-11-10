package slidenerd.vivz.fpam.util;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;

public class CopyUtils {
    public static ArrayList<Group> duplicateGroups(RealmResults<Group> listSourceGroups) {
        ArrayList<Group> listDestinationGroups = new ArrayList<>();
        for (Group sourceGroup : listSourceGroups) {
            Group destinationGroup = new Group(sourceGroup.getGroupId(), sourceGroup.getGroupName(), sourceGroup.getGroupIcon(), sourceGroup.getUnread(), sourceGroup.getLastLoaded(), sourceGroup.isMonitored());
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
        post.setRowId(originalPost.getRowId());
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

    public static Keyword duplicateKeyword(Keyword original) {
        Keyword keyword = new Keyword();
        keyword.setTimestamp(original.getTimestamp());
        keyword.setKeyword(original.getKeyword());
        keyword.setGroups(original.getGroups());
        return keyword;
    }
}
