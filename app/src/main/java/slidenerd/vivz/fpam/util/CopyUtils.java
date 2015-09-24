package slidenerd.vivz.fpam.util;

import java.util.ArrayList;

import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.admin.Picture;
import slidenerd.vivz.fpam.model.json.admin.PictureData;
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

    public static Admin duplicateAdmin(PictureData realmPictureData, Admin realmAdmin) {
        PictureData pictureData = new PictureData(realmPictureData.getUrl(), realmPictureData.getWidth(), realmPictureData.getHeight(), realmPictureData.is_silhouette());
        Picture picture = new Picture(pictureData);
        Admin admin = new Admin(realmAdmin.getId(), realmAdmin.getEmail(), realmAdmin.getFirst_name(), realmAdmin.getLast_name(), picture);
        return admin;
    }

    public static ArrayList<Post> duplicatePosts(RealmResults<Post> realmPosts) {
        ArrayList<Post> listPosts = new ArrayList<>();
        for (Post realmPost : realmPosts) {
            Post post = new Post(realmPost.getId(), realmPost.getFrom(), realmPost.getMessage(), realmPost.getName(), realmPost.getCaption(), realmPost.getDescription(), realmPost.getLink(), realmPost.getPicture(), realmPost.getType(), realmPost.getAttachments(), realmPost.getComments(), realmPost.getUpdated_time());
            listPosts.add(post);
        }
        return listPosts;
    }
}
