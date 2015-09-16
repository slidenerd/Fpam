package slidenerd.vivz.fpam;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.FBAdmin;
import slidenerd.vivz.fpam.model.json.group.FBGroup;
import slidenerd.vivz.fpam.util.FBUtils;
import slidenerd.vivz.fpam.util.NavUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentDrawer extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    /*
    The starting item id for all dynamic items [the loaded groups from Facebook SDK] that are added as part of the menu of the Navigation View. If there are are 6 groups that you loaded from the Facebook SDK, the first group has a menu item id starting with this value, say 101, the last item has a menu item id of 107
    The item from the Drawer previously selected by the user. If the user has not selected anything previously, this defaults to the first item in the drawer
     */
    private static final int MENU_START_ID = 101;
    @InstanceState
    int mSelectedId = 0;
    @InstanceState
    FBAdmin mAdmin;
    /*
        The list of groups that the logged in user is an admin of.
     */
    @InstanceState
    ArrayList<FBGroup> mListGroups = new ArrayList<>();
    private NavigationView mDrawer;
    private Context context;
    private ActivityBase activityBase;
    private Realm realm;

    public FragmentDrawer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activityBase = (ActivityBase) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activityBase = (ActivityBase) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = Realm.getInstance(context);
        mDrawer = (NavigationView) view.findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            mAdmin = DataStore.loadAdmin(context);
            mListGroups = DataStore.loadGroups(realm, context);
        }
        if (mAdmin != null) {
            addHeaderToDrawer(mAdmin);
        }
        addGroupsToDrawer(mListGroups);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigate();
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid admin before attempting to extract any data to prevent a crash.
     *
     * @param admin
     */

    public void addHeaderToDrawer(FBAdmin admin) {
        View headerView = mDrawer.inflateHeaderView(R.layout.drawer_header);
        TextView textUser = (TextView) headerView.findViewById(R.id.text_user);
        textUser.setText(admin.getFirstName() + " " + admin.getLastName());
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid list before adding any items to the drawer to prevent a crash.
     *
     * @param list
     */
    public void addGroupsToDrawer(ArrayList<FBGroup> list) {
        Menu menu = mDrawer.getMenu();
        SubMenu subMenu = menu.addSubMenu(100, 100, 100, R.string.text_my_groups).setIcon(android.R.drawable.ic_menu_info_details);
        if (!hasGroups()) {
            MenuItem item = subMenu.add(100, 100, 100, R.string.text_no_groups);
            item.setIcon(android.R.drawable.stat_notify_error);
        } else {
            int i = MENU_START_ID;
            for (FBGroup group : list) {
                MenuItem item = subMenu.add(100, i, i, group.getName());
                item.setIcon(android.R.drawable.ic_menu_week);
                i++;
            }
        }
        //Bug Fix for the Navigation View not refreshing after items are added dynamically.
        MenuItem mi = menu.getItem(menu.size() - 1);
        mi.setTitle(mi.getTitle());
    }

    /**
     * @return true if a non zero number of groups were retrieved for the currently logged in user , else false
     */
    private boolean hasGroups() {
        return !mListGroups.isEmpty() ? true : false;
    }

    /**
     * The dynamic ID assigned to each group while adding it to the Navigation Drawer. If we have a non zero number of groups, we need to find the position of a group within the List. The dynamic ID starts from a number like 101 and hence if there are 4 groups currently present, their IDS would be 101, 102, 103, 104. If the third group is selected by the user currently from the Navigation Drawer, we get the selected ID as 103.The position of the group with ID 103 is simply calculated as the difference between the selected ID 103 and start ID 101 which turns out to be 2. We check whether the position obtained in the above step is truly within the range of the List before extracting it as an additional measure of precaution.
     *
     * @return the group object corresponding to the id selected by the user.
     */
    @Nullable
    FBGroup getSelectedGroup() {
        FBGroup group = null;
        if (hasGroups()) {
            int position = mSelectedId - MENU_START_ID;
            if (position < mListGroups.size() && position >= 0) {
                group = mListGroups.get(position);
            }
        }
        return group;
    }

    private void logout() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
    }

    boolean navigate() {
        if (mSelectedId == R.id.menu_settings) {

        } else if (mSelectedId == R.id.menu_logout) {
            logout();
            NavUtils.startActivityLogin(context);
            getActivity().finish();
            activityBase.hideDrawer();
        } else {
            FBGroup group = getSelectedGroup();
            if (group != null) {
                activityBase.setTitle(group.getName());
            }
            AccessToken accessToken = FpamApplication.getFacebookAccessToken();
            if (FBUtils.isValidToken(accessToken)) {
                activityBase.loadFeedAsync(FpamApplication.getFacebookAccessToken(), group);
            } else {
                L.m("Did not find a good access token from fragment drawer");
            }
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();
        return navigate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
