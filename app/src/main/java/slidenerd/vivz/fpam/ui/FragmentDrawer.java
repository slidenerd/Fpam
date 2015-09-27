package slidenerd.vivz.fpam.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.parceler.Parcels;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.Constants;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment
public class FragmentDrawer extends Fragment {

    private Admin mAdmin;
    /*
        The list of groups that the logged in user is an admin of.
     */
    private ArrayList<Group> mListGroups = new ArrayList<>();
    private NavigationView mDrawer;
    private Context mContext;
    private ActivityBase mActivity;
    private Realm mRealm;

    public FragmentDrawer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = (ActivityBase) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        this.mActivity = (ActivityBase) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(mContext);
        if (savedInstanceState == null) {
            mAdmin = DataStore.loadAdmin(mRealm);
            mListGroups = DataStore.loadGroups(mRealm);
            L.m("Loading From Realm " + mAdmin.getId() + " " + mAdmin.getEmail() + " " + mAdmin.getFirstName() + " " + mAdmin.getLastName() + " " + mAdmin.getWidth() + " " + mAdmin.getHeight() + " " + mAdmin.getUrl() + " " + mAdmin.isSilhouette());
        } else {
            mAdmin = Parcels.unwrap(savedInstanceState.getParcelable("admin"));
            mListGroups = Parcels.unwrap(savedInstanceState.getParcelable("groups"));
            L.m("Loading From Parceler " + mAdmin.getId() + " " + mAdmin.getEmail() + " " + mAdmin.getFirstName() + " " + mAdmin.getLastName() + " " + mAdmin.getWidth() + " " + mAdmin.getHeight() + " " + mAdmin.getUrl() + " " + mAdmin.isSilhouette());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDrawer = (NavigationView) view.findViewById(R.id.drawer_frame_layout);
        mDrawer.setNavigationItemSelectedListener(mActivity);
        if (mAdmin != null) {
            addHeaderToDrawer(mAdmin);
        }
        addGroupsToDrawer(mListGroups);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("admin", Parcels.wrap(Admin.class, mAdmin));
        outState.putParcelable("groups", Parcels.wrap(mListGroups));
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid admin before attempting to extract any data to prevent a crash.
     *
     * @param admin the person who has logged in using this app
     */

    public void addHeaderToDrawer(@NonNull Admin admin) {
        View headerView = mDrawer.inflateHeaderView(R.layout.drawer_header);
        TextView textUser = (TextView) headerView.findViewById(R.id.text_user);
        textUser.setText(admin.getFirstName() + " " + admin.getLastName());
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid list before adding any items to the drawer to prevent a crash.
     *
     * @param list the list of groups that the logged in user controls
     */
    public void addGroupsToDrawer(ArrayList<Group> list) {
        Menu menu = mDrawer.getMenu();
        SubMenu subMenu = menu.addSubMenu(100, 100, 100, R.string.text_my_groups).setIcon(android.R.drawable.ic_menu_info_details);
        if (mListGroups.isEmpty()) {
            MenuItem item = subMenu.add(100, 100, 100, R.string.text_no_groups);
            item.setIcon(android.R.drawable.stat_notify_error);
        } else {
            int i = Constants.MENU_START_ID;
            for (Group group : list) {
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
     * The dynamic ID assigned to each group while adding it to the Navigation Drawer. If we have a non zero number of groups, we need to find the position of a group within the List. The dynamic ID starts from a number like 101 and hence if there are 4 groups currently present, their IDS would be 101, 102, 103, 104. If the third group is selected by the user currently from the Navigation Drawer, we get the selected ID as 103.The position of the group with ID 103 is simply calculated as the difference between the selected ID 103 and start ID 101 which turns out to be 2. We check whether the position obtained in the above step is truly within the range of the List before extracting it as an additional measure of precaution.
     *
     * @return the group object corresponding to the id selected by the user.
     */
    @Nullable
    public Group getSelectedGroup(int selectedMenuId) {
        Group group = null;
        if (!mListGroups.isEmpty()) {
            int position = selectedMenuId - Constants.MENU_START_ID;
            //if we have a valid position between 0 to number of items in the list, then retrieve the item at that position
            if (position < mListGroups.size() && position >= 0) {
                group = mListGroups.get(position);
            }
        }
        return group;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}