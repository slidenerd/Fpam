package slidenerd.vivz.fpam.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.json.Admin;
import slidenerd.vivz.fpam.model.json.Group;
import slidenerd.vivz.fpam.model.json.Post;
import slidenerd.vivz.fpam.model.pojo.CollectionPayload;
import slidenerd.vivz.fpam.model.pojo.ObjectPayload;

import static slidenerd.vivz.fpam.extras.Constants.FEED_FIELDS;
import static slidenerd.vivz.fpam.extras.Constants.PUBLISH_ACTIONS;
import static slidenerd.vivz.fpam.extras.Constants.READ_PERMISSIONS;
import static slidenerd.vivz.fpam.extras.Constants.SUCCESS;
import static slidenerd.vivz.fpam.extras.Fields.AFTER;
import static slidenerd.vivz.fpam.extras.Fields.CURSORS;
import static slidenerd.vivz.fpam.extras.Fields.DATA;
import static slidenerd.vivz.fpam.extras.Fields.NEXT;
import static slidenerd.vivz.fpam.extras.Fields.PAGING;

public class FBUtils {

    public static final boolean isValid(AccessToken token) {
        return token != null && !token.isExpired();
    }

    /**
     * @param token access token obtained from Facebook
     * @return true if the token is not null , not expired and contains all the read permissions which in our case would be email and groups else return false.
     */
    public static final boolean canRead(AccessToken token) {
        return isValid(token) && token.getPermissions().containsAll(READ_PERMISSIONS);
    }

    public static final boolean canPublish(AccessToken token) {
        return isValid(token) && token.getPermissions().contains(PUBLISH_ACTIONS);
    }

    /**
     * Specify the fields of the logged in user that you are interested to retrieve. Fire a Graph Request synchronously and get its JSON object.
     *
     * @param token An access token needed to start session with Facebook
     * @return an admin object that has all details such as name, email and the profile picture used by the admin.
     */
    @Nullable
    public static ObjectPayload<Admin> loadMe(AccessToken token, Gson gson) throws JSONException {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name,picture.type(normal).width(200).height(200)");
        GraphRequest request = new GraphRequest(token, "me");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        ObjectPayload<Admin> payload = new ObjectPayload<>();
        payload.data = gson.fromJson(response.getJSONObject().toString(), Admin.class);
        payload.error = response.getError();
        return payload;
    }

    /**
     * @param token An access token needed to start session with Facebook
     * @return a List containing all the groups owned by the logged in user and empty list if the logged in user doesn't own any groups or the groups were not retrieved for some reason. The JSON data is actually an object that has an array with the name 'data' which has all the groups.
     * @throws JSONException
     */
    @Nullable
    public static CollectionPayload<Group> loadGroups(AccessToken token, Gson gson) throws JSONException {
        ArrayList<Group> listGroups = new ArrayList<>(Constants.RESULTS_PER_PAGE);
        CollectionPayload<Group> payload = new CollectionPayload<>();
        Bundle parameters = new Bundle();
        TypeToken<ArrayList<Group>> typeToken = new TypeToken<ArrayList<Group>>() {
        };
        //The string that stores the cursor which can take us to the next page
        String cursorAfter = null;
        //Are there more pages in the result? By default we assume false
        boolean hasMorePages = false;
        do {
            GraphRequest request = new GraphRequest(token, "me/admined_groups");
            parameters.putString("fields", "name,id,icon,unread");
            //optionally set a limit of number of groups to fetch per load
//            parameters.putInt("limit", 4);
            //for the first iteration cursorAfter = null and for subsequent iterations, it contains the value of the cursor to visit the next page of results
            parameters.putString("after", cursorAfter);

            request.setParameters(parameters);
            GraphResponse response = request.executeAndWait();
            JSONObject root = response.getJSONObject();
            payload.error = response.getError();
            if (root != null) {

                //Check if our root contains a json array called 'data' that has all the group objects inside it

                if (root.has(DATA) && !root.isNull(DATA)) {

                    //retrieve our json array with group objects
                    JSONArray dataArray = root.getJSONArray(DATA);

                    //For each iteration, we fetch the list of groups and append all of them to what we have so far

                    ArrayList<Group> groups = gson.fromJson(dataArray.toString(), typeToken.getType());
                    listGroups.addAll(groups);
                }

                //Check if our root contains a json object called 'paging'

                if (root.has(PAGING) && !root.isNull(PAGING)) {

                    //retrieve the json object called 'paging'

                    JSONObject paging = root.getJSONObject(PAGING);

                    //check if our 'paging' object has a json object called 'cursors'

                    if (paging != null && paging.has(CURSORS) && !paging.isNull(CURSORS)) {

                        //retrieve the json object called 'cursors'

                        JSONObject cursors = paging.getJSONObject(CURSORS);

                        //check if the cursors object has a field called 'after'

                        if (cursors != null && cursors.has(AFTER) && !cursors.isNull(AFTER)) {

                            //retrieve the field 'after' from our 'cursors'

                            cursorAfter = cursors.getString(AFTER);
                        }
                    }

                    //if our 'paging' object contains a field called 'next' we have more pages to process, else we stop

                    hasMorePages = (paging != null && paging.has(NEXT) && !paging.isNull(NEXT));
                }
            } else {
                //if we did not get a valid data, we have no more pages to process
                hasMorePages = false;
            }
        } while (hasMorePages);
        payload.data = listGroups;
        return payload;
    }

    public static CollectionPayload<Post> loadFeed(AccessToken token, Gson gson, String groupId, int maximum) throws JSONException {
        CollectionPayload<Post> payload = new CollectionPayload<>();
        ArrayList<Post> posts = new ArrayList<>(maximum);
        TypeToken<ArrayList<Post>> typeToken = new TypeToken<ArrayList<Post>>() {
        };
        Bundle parameters = new Bundle();
        parameters.putString("fields", FEED_FIELDS);
        GraphRequest request = new GraphRequest(token, "/" + groupId + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        boolean hasMoreData = false;
        int fetched = 0;
        JSONObject root;
        JSONArray dataArray;
        do {
            root = response.getJSONObject();
            payload.error = response.getError();
            if (root != null) {
                //Check if our root contains a json array called 'data' that has all the group objects inside it
                if (root.has(DATA) && !root.isNull(DATA)) {

                    //retrieve our json array with group objects

                    dataArray = root.getJSONArray(DATA);

                    //For each iteration, we fetch the list of groups and append all of them to what we have so far

                    ArrayList<Post> list = gson.fromJson(dataArray.toString(), typeToken.getType());
                    for (Post post : list) {

                        if (fetched < maximum) {
                            posts.add(post);
                            fetched++;
                        }
                    }
                    request = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                    payload.error = response.getError();
                    //We have more data if posts retrieved in the current iteration are not empty and we have a valid pagination request for the next round and we have not reached the limit or maximum number of posts to retrieve yet

                    hasMoreData = !list.isEmpty() && request != null && fetched != maximum;
                    if (hasMoreData) {
                        request.setParameters(parameters);
                        response = request.executeAndWait();
                    }
                }
            }

        }
        while (hasMoreData);
        payload.data = posts;
        return payload;
    }

    public static CollectionPayload<Post> loadFeedSince(AccessToken token, Gson gson, String groupId, int maximum, long sinceUtc) throws JSONException, FacebookException {
        CollectionPayload<Post> payload = new CollectionPayload<>();
        ArrayList<Post> posts = new ArrayList<>(maximum);
        TypeToken<ArrayList<Post>> type = new TypeToken<ArrayList<Post>>() {
        };
        Bundle parameters = new Bundle();
        parameters.putString("fields", FEED_FIELDS);
        parameters.putString("since", sinceUtc + "");
        GraphRequest request = new GraphRequest(token, "/" + groupId + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        Bundle parametersSubsequent = new Bundle();
        parametersSubsequent.putString("fields", FEED_FIELDS);
        boolean hasMoreData = false;


        int fetched = 0;
        JSONObject root;
        JSONArray dataArray;
        do {
            root = response.getJSONObject();
            payload.error = response.getError();
            if (root != null) {

                //Check if our root contains a json array called 'data' that has all the group objects inside it

                if (root.has(DATA) && !root.isNull(DATA)) {

                    //retrieve our json array with group objects

                    dataArray = root.getJSONArray(DATA);

                    //For each iteration, we fetch the list of groups and append all of them to what we have so far

                    ArrayList<Post> list = gson.fromJson(dataArray.toString(), type.getType());
                    for (Post post : list) {
                        if (fetched < maximum) {
                            posts.add(post);
                            fetched++;
                        }
                    }
                    request = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                    payload.error = response.getError();

                    //We have more data if posts retrieved in the current iteration are not empty and we have a valid pagination request for the next round and we have not reached the limit or maximum number of posts to retrieve yet

                    hasMoreData = !list.isEmpty() && request != null && fetched != maximum;
                    if (hasMoreData) {
                        request.setParameters(parametersSubsequent);
                        response = request.executeAndWait();
                    }
                }
            }
        }
        while (hasMoreData);
        payload.data = posts;
        return payload;
    }

    public static boolean requestDelete(AccessToken token, String postId) throws JSONException {
        GraphRequest request = new GraphRequest(token, postId, null, HttpMethod.DELETE);
        GraphResponse response = request.executeAndWait();
        JSONObject json = response.getJSONObject();
        return json != null && json.has(SUCCESS) && !json.isNull(SUCCESS) && json.getBoolean(SUCCESS);
    }

    public static boolean[] requestDeletes(AccessToken token, ArrayList<String> postIds, int size) throws JSONException {

        GraphRequestBatch requests = new GraphRequestBatch();
        boolean[] statuses = new boolean[size];
        for (int i = 0; i < postIds.size(); i++) {
            String postId = postIds.get(i);
            GraphRequest graphRequest = new GraphRequest(token, postId, null, HttpMethod.DELETE);
            requests.add(graphRequest);
        }
        List<GraphResponse> responses = requests.executeAndWait();
        for (int i = 0; i < postIds.size(); i++) {
            GraphResponse response = responses.get(i);
            JSONObject json = response.getJSONObject();
            boolean success = (json != null && json.has(SUCCESS) && !json.isNull(SUCCESS) && json.getBoolean(SUCCESS));
            statuses[i] = success;
        }
        return statuses;
    }
}
