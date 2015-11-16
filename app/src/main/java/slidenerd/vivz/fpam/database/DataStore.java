package slidenerd.vivz.fpam.database;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.model.json.Post;

public class DataStore {

    public static void limitStoredPosts(Realm realm, String groupId, int maximumPostsStored) {
        RealmResults<Post> results = realm.where(Post.class).beginsWith("postId", groupId).findAllSorted("updatedTime", false);
        int numberOfPostsRemoved = 0;
        realm.beginTransaction();
        while (results.size() > maximumPostsStored) {
            Post post = results.get(results.size() - 1);
            post.removeFromRealm();
            numberOfPostsRemoved++;
        }
        realm.commitTransaction();
        L.m(numberOfPostsRemoved > 0 ? "Removed " + numberOfPostsRemoved : "Nothing to remove");
    }
}