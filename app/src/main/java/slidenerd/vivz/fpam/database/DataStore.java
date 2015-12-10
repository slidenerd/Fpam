package slidenerd.vivz.fpam.database;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.Post;

import static slidenerd.vivz.fpam.extras.Constants.POST_ID;
import static slidenerd.vivz.fpam.extras.Constants.TAG;
import static slidenerd.vivz.fpam.extras.Constants.UPDATED_TIME;

public class DataStore {

    public static void limitStoredPosts(Realm realm, String groupId, int maximumPostsStored) {
        RealmResults<Post> results = realm.where(Post.class).beginsWith(POST_ID, groupId).findAllSorted(UPDATED_TIME, false);
        int removed = 0;
        realm.beginTransaction();

        //Loop in the reverse to remove items from the List without using an Iterator.
        while (results.size() > maximumPostsStored) {
            Post post = results.get(results.size() - 1);
            post.removeFromRealm();
            removed++;
        }
        realm.commitTransaction();
        Log.i(TAG, "limitStoredPosts: " + (removed > 0 ? " Removed " + removed : "Nothing"));
    }
}