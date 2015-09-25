package slidenerd.vivz.fpam.util;

import org.json.JSONException;
import org.json.JSONObject;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;

import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.DATA;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.EMAIL;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.FIRST_NAME;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.HEIGHT;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.ID;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.IS_SILHOUETTE;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.LAST_NAME;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.PICTURE;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.URL;
import static slidenerd.vivz.fpam.util.JSONUtils.AdminJSONFields.WIDTH;

/**
 * Created by vivz on 24/09/15.
 */
public class JSONUtils {
    public static Admin loadAdminFrom(JSONObject adminObject) throws JSONException {
        Admin admin = null;
        //If the feed containsAll all the fields, id email first_name and last_name
        if (containsAll(adminObject, ID, EMAIL, FIRST_NAME, LAST_NAME)) {
            admin = new Admin();
            admin.setId(adminObject.getString(ID));
            admin.setEmail(adminObject.getString(EMAIL));
            admin.setFirstName(adminObject.getString(FIRST_NAME));
            admin.setLastName(adminObject.getString(LAST_NAME));
            //if the feed containsAll picture object
            if (contains(adminObject, PICTURE)) {
                JSONObject pictureObject = adminObject.getJSONObject(PICTURE);
                //if the picture object containsAll data object
                if (contains(pictureObject, DATA)) {
                    JSONObject dataObject = pictureObject.getJSONObject(DATA);
                    //if data object containsAll width, height, is_silhouette and url
                    if (containsAll(dataObject, WIDTH, HEIGHT, IS_SILHOUETTE, URL)) {
                        admin.setWidth(dataObject.getInt(WIDTH));
                        admin.setHeight(dataObject.getInt(HEIGHT));
                        admin.setIsSilhouette(dataObject.getBoolean(IS_SILHOUETTE));
                        admin.setUrl(dataObject.getString(URL));
                    }
                }
            }
        }
        return admin;
    }

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
}
