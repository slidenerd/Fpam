package slidenerd.vivz.fpam.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.JSONUtils.GroupFields;

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
    public static ArrayList<Group> requestGroupsSync(AccessToken accessToken, Gson gson) throws JSONException {
        ArrayList<Group> listGroups = new ArrayList<>();
        Bundle parameters = new Bundle();
        TypeToken<ArrayList<Group>> typeToken = new TypeToken<ArrayList<Group>>() {
        };
        //The string that stores the cursor which can take us to the next page
        String cursorAfter = null;
        //Are there more pages in the result? By default we assume false
        boolean hasMorePages = false;
        do {
            GraphRequest request = new GraphRequest(accessToken, "me/admined_groups");
            parameters.putString("fields", "name,id,icon,unread");
            //optionally set a limit of number of groups to fetch per load
//            parameters.putInt("limit", 4);
            //for the first iteration cursorAfter = null and for subsequent iterations, it contains the value of the cursor to visit the next page of results
            parameters.putString("after", cursorAfter);

            request.setParameters(parameters);
            GraphResponse response = request.executeAndWait();
            JSONObject root = response.getJSONObject();
            if (root != null) {

                //Check if our root contains a json array called 'data' that has all the group objects inside it

                if (root.has(GroupFields.DATA) && !root.isNull(GroupFields.DATA)) {

                    //retrieve our json array with group objects
                    JSONArray dataArray = root.getJSONArray(GroupFields.DATA);

                    //For each iteration, we fetch the list of groups and append all of them to what we have so far

                    ArrayList<Group> groups = gson.fromJson(dataArray.toString(), typeToken.getType());
                    listGroups.addAll(groups);
                }

                //Check if our root contains a json object called 'paging'

                if (root.has(GroupFields.PAGING) && !root.isNull(GroupFields.PAGING)) {

                    //retrieve the json object called 'paging'

                    JSONObject paging = root.getJSONObject(GroupFields.PAGING);

                    //check if our 'paging' object has a json object called 'cursors'

                    if (paging != null && paging.has(GroupFields.CURSORS) && !paging.isNull(GroupFields.CURSORS)) {

                        //retrieve the json object called 'cursors'

                        JSONObject cursors = paging.getJSONObject(GroupFields.CURSORS);

                        //check if the cursors object has a field called 'after'

                        if (cursors != null && cursors.has(GroupFields.AFTER) && !cursors.isNull(GroupFields.AFTER)) {

                            //retrieve the field 'after' from our 'cursors'

                            cursorAfter = cursors.getString(GroupFields.AFTER);
                        }
                    }

                    //if our 'paging' object contains a field called 'next' we have more pages to process, else we stop

                    hasMorePages = (paging != null && paging.has(GroupFields.NEXT) && !paging.isNull(GroupFields.NEXT));
                }
            } else {
                //if we did not get a valid response, we have no more pages to process
                hasMorePages = false;
            }
        } while (hasMorePages);
        return listGroups;
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

    public static boolean requestDeletePost(AccessToken accessToken, String postId) throws JSONException {
        GraphRequest graphRequest = new GraphRequest(accessToken, postId, null, HttpMethod.DELETE);
        GraphResponse response = graphRequest.executeAndWait();
        JSONObject jsonObject = response.getJSONObject();
        return jsonObject != null && jsonObject.has("success") && !jsonObject.isNull("success") && jsonObject.getBoolean("success");
    }
}
