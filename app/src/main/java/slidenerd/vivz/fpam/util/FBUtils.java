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

import java.util.ArrayList;

import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;

public class FBUtils {
    //TODO handle errors that may arise if JSONObject is null while retrieving admin
    public static boolean isValidToken(AccessToken accessToken) {
        return accessToken != null && !accessToken.isExpired();
    }

    /**
     * Specify the fields of the logged in user that you are interested to retrieve. Fire a Graph Request synchronously and get its JSON object.
     *
     * @param accessToken An access token needed to start session with Facebook
     * @return an admin object that has all details such as name, email and the profile picture used by the admin.
     */
    @Nullable
    public static Admin requestMeSync(AccessToken accessToken, Gson gson) throws JSONException {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,picture.type(normal).width(200).height(200)");
        GraphRequest request = new GraphRequest(accessToken, "me");
        request.setParameters(parameters);
        GraphResponse graphResponse = request.executeAndWait();
        return gson.fromJson(graphResponse.getJSONObject().toString(), Admin.class);
    }

    /**
     * @param accessToken An access token needed to start session with Facebook
     * @return a List containing all the groups owned by the logged in user and empty list if the logged in user doesn't own any groups or the groups were not retrieved for some reason. The JSON response is actually an object that has an array with the name 'data' which has all the groups.
     * @throws JSONException
     */
    @Nullable
    public static ArrayList<Group> requestGroupsSync(AccessToken accessToken, Gson gson, String adminId) throws JSONException {
        GraphRequest request = new GraphRequest(accessToken, "me/admined_groups");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,icon,unread");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        JSONObject root = response.getJSONObject();
        ArrayList<Group> listGroups = new ArrayList<>();
        if (root != null && root.has(JSONUtils.Groups.DATA) && !root.isNull(JSONUtils.Groups.DATA)) {
            JSONArray dataArray = root.getJSONArray(JSONUtils.Groups.DATA);
            TypeToken<ArrayList<Group>> typeToken = new TypeToken<ArrayList<Group>>() {
            };
            listGroups = gson.fromJson(dataArray.toString(), typeToken.getType());
        }
        return listGroups;
    }

    /**
     * @param accessToken An access token needed to start session with Facebook
     * @return a List containing all the groups owned by the logged in user and empty list if the logged in user doesn't own any groups or the groups were not retrieved for some reason. The JSON response is actually an object that has an array with the name 'data' which has all the groups.
     * @throws JSONException
     */
    @Nullable
    public static JSONObject requestGroupsSync(AccessToken accessToken) throws JSONException {
        GraphRequest request = new GraphRequest(accessToken, "me/admined_groups");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,icon,unread");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        ArrayList<Group> listGroups = new ArrayList<>();
        return response.getJSONObject();
    }

    /*
    TODO implement the since parameter for requesting feeds from Facebook Graph API
     */
    public static JSONObject requestFeedSync(AccessToken token, Group group) throws JSONException {
        ArrayList<Post> listPosts = new ArrayList<>();
        Bundle parameters = new Bundle();
        parameters.putString("fields", "from,message,caption,comments{from,message},description,name,picture,type,updated_time,attachments{media,type,url},link");
        parameters.putString("limit", "15");
//        parameters.putLong("since", 1442552400);
        GraphRequest request = new GraphRequest(token, "/" + group.getId() + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        return response.getJSONObject();
    }
}
