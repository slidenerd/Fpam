package slidenerd.vivz.fpam.extras;

/**
 * Created by vivz on 27/09/15.
 */
public interface Constants {

    String PERMISSION_EMAIL = "email";
    String PERMISSION_GROUPS = "user_managed_groups";
    String PUBLISH_ACTIONS = "publish_actions";
    int RESULTS_PER_PAGE = 25;
    int DEFAULT_NUMBER_OF_ITEMS_TO_FETCH = 25;
    /*
    The starting item id for all dynamic items [the loaded groups from Facebook SDK] that are added as part of the menu of the Navigation View. If there are are 6 groups that you loaded from the Facebook SDK, the first group has a menu item id starting with this value, say 101, the last item has a menu item id of 107
    The item from the FragmentDrawer previously selected by the user. If the user has not selected anything previously, this defaults to the first item in the nav_drawer
     */
    int MENU_START_ID = 101;

    String GROUP_ID_NONE = "NONE";
    int NA = -1;
}
