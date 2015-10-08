package slidenerd.vivz.fpam.background;

import android.app.IntentService;
import android.os.Parcelable;
import android.util.Pair;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.Fpam;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Spammer;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;


/**
 * TODO fix the illegal state exception thrown by realm by deleting the last post id
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
@EIntentService
public class FilterPostService extends AbstractIntentService {

    @App
    Fpam mApplication;

    public FilterPostService() {
        super("Filter Spam Posts Service");
    }

    @ServiceAction
    public void onFilterPosts(Parcelable groupParcelable) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Group selectedGroup = Parcels.unwrap(groupParcelable);
            //find the posts for this group
            RealmResults<Post> realmResults = realm.where(Post.class).beginsWith("postId", selectedGroup.getId()).findAllSorted("updatedTime", false);
            //find all the spammers
            RealmResults<Spammer> spammers = realm.where(Spammer.class).findAll();
            //check if a post was made by a spammer

            ArrayList<String> listDeletableIds = new ArrayList<>();
            for (Post post : realmResults) {
                for (Spammer spammer : spammers) {
                    if (spammer.getUserGroupCompositeId().startsWith(post.getUserId())) {
                        //this post was made by a spammer
                        listDeletableIds.add(post.getPostId());
                        break;
                    }
                }
            }

            try {
                if (listDeletableIds.isEmpty()) return;
                ArrayList<Pair<String, Boolean>> listStatus = FBUtils.requestDeletePosts(mApplication.getToken(), listDeletableIds);
                realm.beginTransaction();
                for (Pair<String, Boolean> pair : listStatus) {
                    if (pair.second) {
                        realm.where(Post.class).equalTo("postId", pair.first).findFirst().removeFromRealm();
                    }
                }
                realm.commitTransaction();
            } catch (JSONException e) {
                L.m(e + "");
            }

        } finally {
            realm.close();
        }
        NavUtils.broadcastPostsFiltered(this);
    }
}
