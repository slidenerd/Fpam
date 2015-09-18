package slidenerd.vivz.fpam.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import slidenerd.vivz.fpam.Keys;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 29/07/15.
 */
public class FBUtils {
    public static boolean isValidToken(AccessToken accessToken) {
        return accessToken != null && !accessToken.isExpired();
    }

    /**
     * Specify the fields of the logged in user that you are interested to retrieve. Fire a Graph Request synchronously and get its JSON object.
     *
     * @param accessToken An access token needed to start session with Facebook
     * @return an admin object that contains all details such as name, email and the profile picture used by the admin.
     */
    @Nullable
    public static Admin requestMeSync(AccessToken accessToken, Gson gson) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,picture.type(normal).width(200).height(200)");

        GraphRequest request = new GraphRequest(accessToken, "me");
        request.setParameters(parameters);
        GraphResponse graphResponse = request.executeAndWait();
        JSONObject jsonObject = graphResponse.getJSONObject();
        Admin admin = gson.fromJson(jsonObject.toString(), Admin.class);
        return admin;
    }


    /**
     * @param accessToken An access token needed to start session with Facebook
     * @return a List containing all the groups owned by the logged in user and empty list if the logged in user doesn't own any groups or the groups were not retrieved for some reason. The JSON response is actually an object that contains an array with the name 'data' which contains all the groups.
     * @throws JSONException
     */
    public static ArrayList<Group> requestGroupsSync(AccessToken accessToken, Gson gson) throws JSONException {
        GraphRequest request = new GraphRequest(accessToken, "me/admined_groups");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,icon,unread");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        ArrayList<Group> listGroups = new ArrayList<>();
        JSONObject jsonObject = response.getJSONObject();
        JSONArray arrayData = jsonObject.getJSONArray(Keys.JSON_KEY_DATA);
        for (int i = 0; i < arrayData.length(); i++) {
            try {
                JSONObject objectGroup = arrayData.getJSONObject(i);
                Group group = gson.fromJson(objectGroup.toString(), Group.class);
                listGroups.add(group);
            } catch (JSONException e) {
                L.m("" + e);
            }
        }
        return listGroups;
    }

    /*
    TODO implement the since parameter for requesting feeds from Facebook Graph API
     */
    public static ArrayList<Post> requestFeedSync(AccessToken token, Gson gson, Group group) throws JSONException {
        ArrayList<Post> listPosts = new ArrayList<>();
        Bundle parameters = new Bundle();
        parameters.putString("fields", "from,message,caption,comments{from,message},description,name,picture,type,updated_time,attachments{media,type,url},link");
        parameters.putString("limit", "15");
        parameters.putLong("since", 1442552400);
        GraphRequest request = new GraphRequest(token, "/" + group.getId() + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        JSONObject jsonObject = response.getJSONObject();
        JSONArray arrayData = jsonObject.getJSONArray(Keys.JSON_KEY_DATA);
        Type listType = new TypeToken<ArrayList<Post>>() {
        }.getType();
        L.m(arrayData.toString());
        listPosts = gson.fromJson(arrayData.toString(), listType);
        return listPosts;
    }
}
