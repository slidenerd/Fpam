package slidenerd.vivz.fpam.extras;

/**
 * Created by vivz on 27/09/15.
 */
public interface Constants {

    int LESS = -1;
    int GREATER = 1;
    int EQUAL = 0;

    String FEED_FIELDS = "from{name,id,picture},message,caption,comments{from,message},description,name,full_picture,type,updated_time,to{id},link,created_time";

    String GROUP_ID = "groupId";
    String GROUP_NAME = "groupName";
    String POST_ID = "postId";
    String KEYWORD = "keyword";
    String POSTLYTICS_ID = "compositeGroupDateId";
    String SPAMMER_ID = "compositeUserGroupId";

    int TOP_ENTRIES_COUNT = 3;
    String ACTION_LOAD_FEED = "slidenerd.vivz.fpam.action.LOAD_FEED";
    String ACTION_DELETE_POST = "slidenerd.vivz.fpam.action.DELETE_POST";
    String ACTION_DELETE_RESPONSE = "slidenerd.vivz.fpam.action.DELETE_STATUS";
    String EXTRA_SELECTED_GROUP = "slidenerd.vivz.fpam.action.EXTRA_SELECTED_GROUP";
    String EXTRA_POSITION = "slidenerd.vivz.fpam.extra.POSITION";
    String EXTRA_ID = "slidenerd.vivz.fpam.extra.ID";
    String EXTRA_OUTCOME = "slidenerd.vivz.fpam.extra.OUTCOME";
    String PERMISSION_EMAIL = "email";
    String PERMISSION_GROUPS = "user_managed_groups";
    String[] READ_PERMISSIONS = new String[]{PERMISSION_EMAIL, PERMISSION_GROUPS};
    String PUBLISH_ACTIONS = "publish_actions";
    int RESULTS_PER_PAGE = 25;
    int DEFAULT_NUMBER_OF_ITEMS_TO_FETCH = 50;
    /*
    The starting item id for all dynamic items [the loaded groups from Facebook SDK] that are added as part of the menu of the Navigation View. If there are are 6 groups that you loaded from the Facebook SDK, the first group has a menu item id starting with this value, say 101, the last item has a menu item id of 107
    The item from the FragmentDrawer previously selected by the user. If the user has not selected anything previously, this defaults to the first item in the nav_drawer
     */
    int MENU_START_ID = 101;

    String GROUP_ID_NONE = "NONE";
    int NA = -1;
}
