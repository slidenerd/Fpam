package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.settings.ActivityKeywords_;
import slidenerd.vivz.fpam.settings.SettingsActivity_;
import slidenerd.vivz.fpam.ui.ActivityCache_;
import slidenerd.vivz.fpam.ui.ActivityLogin_;
import slidenerd.vivz.fpam.ui.ActivityMain_;

/**
 * Created by vivz on 28/07/15.
 */
public class NavUtils {
    public static final String ACTION_LOAD_FEED = "load_feed";
    public static final String EXTRA_SELECTED_GROUP = "selected_group";

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

    public static void startActivityKeywords(Context context) {
        ActivityKeywords_.intent(context).start();
    }
}
