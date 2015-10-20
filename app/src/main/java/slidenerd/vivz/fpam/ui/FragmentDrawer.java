package slidenerd.vivz.fpam.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.parceler.Parcels;

import java.util.ArrayList;

import io.realm.Realm;
import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.database.DataStore;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.MyPrefs_;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.widget.RoundedImageView;

/**
 * TODO show the drawer if its the first time else hide it, make the logout and settings work, align the images inside the drawer
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.nav_drawer)
public class FragmentDrawer extends Fragment {

    @ViewById(R.id.nav_view)
    NavigationView mDrawer;
    @Pref
    MyPrefs_ mPref;
    /*
        The list of groups that the logged in user is an admin of.
     */
    private ArrayList<Group> mGroups = new ArrayList<>();
    private Admin mAdmin;
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
            mAdmin = DataStore.copyLoadAdmin(mRealm);
            mGroups = DataStore.copyLoadGroups(mRealm);
            L.m("Loading From Realm ");
        } else {
            mAdmin = Parcels.unwrap(savedInstanceState.getParcelable("admin"));
            mGroups = Parcels.unwrap(savedInstanceState.getParcelable("groups"));
            L.m("Loading From Parceler ");
        }
    }

    @AfterViews
    public void onViewCreated() {
        mDrawer.setNavigationItemSelectedListener(mActivity);
        if (mAdmin != null) {
            addHeaderToDrawer(mAdmin);
        }
        addGroupsToDrawer(mGroups);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("admin", Parcels.wrap(Admin.class, mAdmin));
        outState.putParcelable("groups", Parcels.wrap(mGroups));
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid admin before attempting to extract any data to prevent a crash.
     *
     * @param admin the person who has logged in using this app
     */

    public void addHeaderToDrawer(@NonNull Admin admin) {
        View headerView = mDrawer.inflateHeaderView(R.layout.nav_header_main);
        TextView textUserName = (TextView) headerView.findViewById(R.id.text_username);
        RoundedImageView imageProfile = (RoundedImageView) headerView.findViewById(R.id.image_profile);
        textUserName.setText(admin.getName());
        Picasso.with(mContext).load(admin.getUrl()).into(imageProfile);
    }

    /**
     * If the access token is null or expired, this Activity will finish executing but this method is still called inside the onCreate as per the debugger and hence, check whether we have a valid list before adding any items to the nav_drawer to prevent a crash.
     *
     * @param list the list of groups that the logged in user controls
     */
    public void addGroupsToDrawer(ArrayList<Group> list) {
        Menu menu = mDrawer.getMenu();
        if (mGroups.isEmpty()) {
            MenuItem item = menu.add(100, 100, 100, R.string.text_groups_none);
            item.setIcon(android.R.drawable.stat_notify_error);
        } else {
            int i = Constants.MENU_START_ID;
            for (Group group : list) {
                final MenuItem item = menu.add(100, i, i, group.getName());
                item.setIcon(R.drawable.ic_group_black);
                i++;
            }

            //Add an extra item at the bottom to prevent the navigationview from drawing a divider between the last item and settings

            menu.add(100, i, i, Constants.GROUP_ID_NONE);
        }
        //Bug Fix for the Navigation View not refreshing after items are added dynamically.
        MenuItem mi = menu.getItem(menu.size() - 1);
        mi.setTitle(mi.getTitle());
    }

    @Click(R.id.action_settings)
    public void onSettingsClick() {
        mActivity.onSettingsSelected();
    }

    @Click(R.id.action_logout)
    public void onLogoutClick() {
        mActivity.logout();
    }

    /**
     * The dynamic ID assigned to each group while adding it to the Navigation FragmentDrawer. If we have a non zero number of groups, we need to find the position of a group within the List. The dynamic ID starts from a number like 101 and hence if there are 4 groups currently present, their IDS would be 101, 102, 103, 104. If the third group is selected by the user currently from the Navigation FragmentDrawer, we get the selected ID as 103.The position of the group with ID 103 is simply calculated as the difference between the selected ID 103 and start ID 101 which turns out to be 2. We check whether the position obtained in the above step is truly within the range of the List before extracting it as an additional measure of precaution.
     *
     * @return the group object corresponding to the id selected by the user.
     */
    @Nullable
    public Group getSelectedGroup(int selectedMenuId) {
        int selectedPosition = selectedMenuId - Constants.MENU_START_ID;
        return !mGroups.isEmpty() && selectedPosition >= 0 && selectedPosition < mGroups.size() ? mGroups.get(selectedPosition) : null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}