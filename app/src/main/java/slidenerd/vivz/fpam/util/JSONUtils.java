package slidenerd.vivz.fpam.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.json.group.Groups;

/**
 * Created by vivz on 24/09/15.
 */
public class JSONUtils {
    public static boolean contains(JSONObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key)) {
            return true;
        } else {
            L.m("Did not find key " + key + " inside JSON object " + jsonObject);
            return false;
        }
    }

    public static boolean containsAll(JSONObject jsonObject, String... keys) {
        boolean containsAllKeys = true;
        for (int i = 0; i < keys.length; i++) {
            containsAllKeys = containsAllKeys && contains(jsonObject, keys[i]);
        }
        return containsAllKeys;
    }

    @Nullable
    public static Admin loadAdminFrom(JSONObject adminObject) throws JSONException {
        Admin admin = null;
        //If the feed containsAll all the fields, id email first_name and last_name
        if (containsAll(adminObject, AdminJSONFields.ID, AdminJSONFields.EMAIL, AdminJSONFields.FIRST_NAME, AdminJSONFields.LAST_NAME)) {
            admin = new Admin();
            admin.setId(adminObject.getString(AdminJSONFields.ID));
            admin.setEmail(adminObject.getString(AdminJSONFields.EMAIL));
            admin.setFirstName(adminObject.getString(AdminJSONFields.FIRST_NAME));
            admin.setLastName(adminObject.getString(AdminJSONFields.LAST_NAME));
            //if the feed containsAll picture object
            if (contains(adminObject, AdminJSONFields.PICTURE)) {
                JSONObject pictureObject = adminObject.getJSONObject(AdminJSONFields.PICTURE);
                //if the picture object containsAll data object
                if (contains(pictureObject, AdminJSONFields.DATA)) {
                    JSONObject dataObject = pictureObject.getJSONObject(AdminJSONFields.DATA);
                    //if data object containsAll width, height, is_silhouette and url
                    if (containsAll(dataObject, AdminJSONFields.WIDTH, AdminJSONFields.HEIGHT, AdminJSONFields.IS_SILHOUETTE, AdminJSONFields.URL)) {
                        admin.setWidth(dataObject.getInt(AdminJSONFields.WIDTH));
                        admin.setHeight(dataObject.getInt(AdminJSONFields.HEIGHT));
                        admin.setIsSilhouette(dataObject.getBoolean(AdminJSONFields.IS_SILHOUETTE));
                        admin.setUrl(dataObject.getString(AdminJSONFields.URL));
                    }
                }
            }
        }
        return admin;
    }

    @NonNull
    public static Groups loadGroupsFrom(JSONObject groupsObject) throws JSONException {
        Groups groups = new Groups();
        RealmList<Group> listGroups = new RealmList<>();
        if (contains(groupsObject, GroupsJSONFields.DATA)) {
            JSONArray arrayData = groupsObject.getJSONArray(GroupsJSONFields.DATA);
            for (int i = 0; i < arrayData.length(); i++) {
                JSONObject groupObject = arrayData.getJSONObject(i);
                if (containsAll(groupObject, GroupsJSONFields.ID, GroupsJSONFields.ICON, GroupsJSONFields.UNREAD, GroupsJSONFields.NAME)) {
                    Group group = new Group();
                    group.setId(groupObject.getString(GroupsJSONFields.ID));
                    group.setName(groupObject.getString(GroupsJSONFields.NAME));
                    group.setIcon(groupObject.getString(GroupsJSONFields.ICON));
                    group.setUnread(groupObject.getInt(GroupsJSONFields.UNREAD));
                    listGroups.add(group);
                }
            }
        }
        if (contains(groupsObject, GroupsJSONFields.PAGING)) {
            JSONObject pagingObject = groupsObject.getJSONObject(GroupsJSONFields.PAGING);
            if (contains(pagingObject, GroupsJSONFields.CURSORS)) {
                JSONObject cursorsObject = pagingObject.getJSONObject(GroupsJSONFields.CURSORS);
                if (containsAll(cursorsObject, GroupsJSONFields.BEFORE, GroupsJSONFields.AFTER)) {
                    groups.setBefore(cursorsObject.getString(GroupsJSONFields.BEFORE));
                    groups.setAfter(cursorsObject.getString(GroupsJSONFields.AFTER));
                }
            }
        }
        if (contains(groupsObject, GroupsJSONFields.PREVIOUS)) {
            groups.setPrevious(groupsObject.getString(GroupsJSONFields.PREVIOUS));
        }
        if (contains(groupsObject, GroupsJSONFields.NEXT)) {
            groups.setNext(groupsObject.getString(GroupsJSONFields.NEXT));
        }
        groups.setTimestamp(System.currentTimeMillis());
        groups.setGroups(listGroups);
        return groups;
    }

    /**
     * The feed shown below is admin JSON object
     * {
     * "id": "867531740000500",
     * "email": "vivek.officialr@gmail.com",
     * "first_name": "Vladimir",
     * "last_name": "Makarov",
     * "picture":
     * {
     * "data":
     * {
     * "height": 200,
     * "is_silhouette": false,
     * "url": "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xfa1/v/t1.0-1/p200x200/11949472_882200231866984_5133040776625725290_n.jpg?oh=dd1440ca1c282007531a55262e4720b9&oe=5699F731&__gda__=1453635768_835099a37947e7b2bc08713c52cecbdd",
     * "width": 200
     * }
     * }
     * }
     */
    public interface AdminJSONFields {
        String ID = "id";
        String EMAIL = "email";
        String FIRST_NAME = "first_name";
        String LAST_NAME = "last_name";
        String PICTURE = "picture";
        String DATA = "data";
        String WIDTH = "width";
        String HEIGHT = "height";
        String URL = "url";
        String IS_SILHOUETTE = "is_silhouette";
    }

    /**
     * {
     * "data": [
     * {
     * "name": "The Fantastic Four",
     * "id": "368380276678573",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yQ/r/QSvrimiDFPQ.png",
     * "unread": 1
     * },
     * {
     * "name": "C/C++ and Native Development",
     * "id": "1704641256427590",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yB/r/odyq1oFT40B.png",
     * "unread": 0
     * }
     * ],
     * "paging": {
     * "cursors": {
     * "before": "MzY4MzgwMjc2Njc4NTcz",
     * "after": "MTcwNDY0MTI1NjQyNzU5MAZDZD"
     * },
     * "next": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&after=MTcwNDY0MTI1NjQyNzU5MAZDZD",
     * "previous": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&before=MzY4MzgwMjc2Njc4NTcz"
     * }
     * }
     */
    public interface GroupsJSONFields {
        String DATA = "data";
        String ID = "id";
        String NAME = "name";
        String ICON = "icon";
        String UNREAD = "unread";
        String PAGING = "paging";
        String CURSORS = "cursors";
        String BEFORE = "before";
        String AFTER = "after";
        String NEXT = "next";
        String PREVIOUS = "previous";
    }
}
