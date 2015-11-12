package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.settings.ActivityKeywords_;
import slidenerd.vivz.fpam.settings.SettingsActivity_;
import slidenerd.vivz.fpam.ui.ActivityCache_;
import slidenerd.vivz.fpam.ui.ActivityLogin_;
import slidenerd.vivz.fpam.ui.ActivityMain_;

import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_POST;
import static slidenerd.vivz.fpam.extras.Constants.ACTION_DELETE_STATUS;
import static slidenerd.vivz.fpam.extras.Constants.ACTION_LOAD_FEED;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_OUTCOME;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POSITION;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_POST;
import static slidenerd.vivz.fpam.extras.Constants.EXTRA_SELECTED_GROUP;

/**
 * Created by vivz on 28/07/15.
 */
public class NavUtils {


    public static void startActivityLogin(Context context) {
        ActivityLogin_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

    public static void startActivityCache(Context context) {
        ActivityCache_.intent(context).start();
    }

    public static void startActivitySettings(Context context) {
        SettingsActivity_.intent(context).start();
    }

    public static void startActivityMain(Context context) {
        ActivityMain_.intent(context).start();
    }

    public static void broadcastSelectedGroup(Context context, Group group, boolean filterPosts) {
        Intent intent = new Intent(ACTION_LOAD_FEED);
        intent.putExtra(EXTRA_SELECTED_GROUP, Parcels.wrap(Group.class, group));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastRequestDelete(Context context, int position, Post post) {
        Intent intent = new Intent(ACTION_DELETE_POST);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_POST, Parcels.wrap(Post.class, post));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void startActivityKeywords(Context context) {
        ActivityKeywords_.intent(context).start();
    }

    public static void broadcastDeleteStatus(Context context, boolean outcome, int position) {
        Intent intent = new Intent(ACTION_DELETE_STATUS);
        intent.putExtra(EXTRA_OUTCOME, outcome);
        intent.putExtra(EXTRA_POSITION, position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
