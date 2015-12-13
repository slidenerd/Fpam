package slidenerd.vivz.fpam.extras;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vivz on 27/09/15.
 */
public interface Constants {

    /**
     * The TAG used for logging purposes
     */
    String TAG = "VIVZ";
    /**
     * The constant LESS.
     */
    int LESS = -1;
    /**
     * The constant GREATER.
     */
    int GREATER = 1;

    /**
     * The monitoring frequency
     */
    int PERIOD = 1800000;

    /**
     * The constant KEY_LAST_LOADED_PREFIX.
     */
    String KEY_LAST_LOADED_PREFIX = "last_loaded_";
    /**
     * The constant KEY_MONITORED_PREFIX.
     */
    String KEY_MONITORED_PREFIX = "monitored_";
    /**
     * The constant FEED_FIELDS.
     */
    String FEED_FIELDS = "from{name,id,picture},message,caption,comments{from,message},description,name,full_picture,type,updated_time,to{id},link,created_time";

    /**
     * The constant GROUP_ID.
     */
    String GROUP_ID = "groupId";
    /**
     * The constant GROUP_NAME.
     */
    String GROUP_NAME = "groupName";
    /**
     * The constant POST_ID.
     */
    String POST_ID = "postId";
    /**
     * The constant KEYWORD.
     */
    String KEYWORD = "keyword";
    /**
     * The constant POSTLYTICS_ID.
     */
    String POSTLYTICS_ID = "compositeGroupDateId";
    /**
     * The constant SPAMMER_ID.
     */
    String SPAMMER_ID = "compositeGroupUserId";
    /**
     * The constant UPDATED_TIME.
     */
    String UPDATED_TIME = "updatedTime";
    /**
     * The constant COMPOSITE_GROUP_ORDER_ID.
     */
    String COMPOSITE_GROUP_ORDER_ID = "compositeGroupOrderId";
    /**
     * The constant COMPOSITE_GROUP_USER_ID.
     */
    String COMPOSITE_GROUP_USER_ID = "compositeGroupUserId";
    /**
     * The constant COUNT.
     */
    String COUNT = "count";
    /**
     * The constant SUCCESS.
     */
    String SUCCESS = "success";
    /**
     * The constant GROUPS.
     */
    String GROUPS = "groups";

    /**
     * The constant TOP_ENTRIES_COUNT.
     */
    int TOP_ENTRIES_COUNT = 3;
    /**
     * The constant ACTION_LOAD_FEED.
     */
    String ACTION_LOAD_FEED = "slidenerd.vivz.fpam.action.LOAD_FEED";
    /**
     * The constant ACTION_DELETE_POST.
     */
    String ACTION_DELETE_POST = "slidenerd.vivz.fpam.action.DELETE_POST";
    /**
     * The constant ACTION_DELETE_RESPONSE.
     */
    String ACTION_DELETE_RESPONSE = "slidenerd.vivz.fpam.action.DELETE_STATUS";
    /**
     * The constant EXTRA_SELECTED_GROUP.
     */
    String EXTRA_SELECTED_GROUP = "slidenerd.vivz.fpam.action.EXTRA_SELECTED_GROUP";
    /**
     * The constant EXTRA_POSITION.
     */
    String EXTRA_POSITION = "slidenerd.vivz.fpam.extra.POSITION";
    /**
     * The constant EXTRA_ID.
     */
    String EXTRA_ID = "slidenerd.vivz.fpam.extra.ID";
    /**
     * The constant EXTRA_OUTCOME.
     */
    String EXTRA_OUTCOME = "slidenerd.vivz.fpam.extra.OUTCOME";
    /**
     * The constant PERMISSION_EMAIL.
     */
    String PERMISSION_EMAIL = "email";
    /**
     * The constant PERMISSION_GROUPS.
     */
    String PERMISSION_GROUPS = "user_managed_groups";
    /**
     * The Read permissions.
     */
    List<String> READ_PERMISSIONS = Arrays.asList(PERMISSION_EMAIL, PERMISSION_GROUPS);
    /**
     * The constant PUBLISH_ACTIONS.
     */
    String PUBLISH_ACTIONS = "publish_actions";
    /**
     * The constant RESULTS_PER_PAGE.
     */
    int RESULTS_PER_PAGE = 25;
    /**
     * The constant DEFAULT_NUMBER_OF_ITEMS_TO_FETCH.
     */
    int DEFAULT_NUMBER_OF_ITEMS_TO_FETCH = 25;
    /**
     * The constant MENU_START_ID.
     */
/*
    The starting data id for all dynamic data [the loaded groups from Facebook SDK] that are added as part of the menu of the Navigation View. If there are are 6 groups that you loaded from the Facebook SDK, the first group has a menu data id starting with this value, say 101, the last data has a menu data id of 107
    The data from the FragmentDrawer previously selected by the user. If the user has not selected anything previously, this defaults to the first data in the nav_drawer
     */
    int MENU_START_ID = 101;

    /**
     * The constant GROUP_NONE.
     */
    String GROUP_NONE = "NONE";
    /**
     * The constant NA.
     */
    int NA = -1;
    /**
     * The constant ALL.
     */
    String ALL = "ALL";
    String TEXT_REQUEST_PERMISSION = "request_permission";
}
