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

import io.realm.RealmResults;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.extras.FeedFields;
import slidenerd.vivz.fpam.extras.GroupFields;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.pojo.DeleteResponseInfo;

public class FBUtils {

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
        ArrayList<Group> listGroups = new ArrayList<>(Constants.RESULTS_PER_PAGE);
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

    public static ArrayList<Post> requestFeedFirstTime(AccessToken token, Gson gson, Group group) throws JSONException {
        ArrayList<Post> listPosts = new ArrayList<>(Constants.RESULTS_PER_PAGE);
        TypeToken<ArrayList<Post>> typeToken = new TypeToken<ArrayList<Post>>() {
        };
        Bundle parameters = new Bundle();
        parameters.putString("fields", "from{name,id,picture},message,caption,comments{from,message},description,name,full_picture,type,updated_time,attachments{type},link,created_time");
        GraphRequest request = new GraphRequest(token, "/" + group.getId() + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        JSONObject root = response.getJSONObject();
        if (root != null) {
            //Check if our root contains a json array called 'data' that has all the group objects inside it
            if (root.has(FeedFields.DATA) && !root.isNull(FeedFields.DATA)) {

                //retrieve our json array with group objects
                JSONArray dataArray = root.getJSONArray(GroupFields.DATA);

                //For each iteration, we fetch the list of groups and append all of them to what we have so far

                listPosts = gson.fromJson(dataArray.toString(), typeToken.getType());
            }
        }
        return listPosts;
    }

    public static ArrayList<Post> requestFeedSince(AccessToken token, Gson gson, Group group, int maximumNumberOfPostsToRetrieve, long sinceUnixEpoch) throws JSONException, FacebookException {
        ArrayList<Post> listPosts = new ArrayList<>(maximumNumberOfPostsToRetrieve);
        TypeToken<ArrayList<Post>> typeToken = new TypeToken<ArrayList<Post>>() {
        };
        Bundle parameters = new Bundle();
        parameters.putString("fields", "from{name,id,picture},message,caption,comments{from,message},description,name,full_picture,type,updated_time,attachments{type},link,created_time");
        parameters.putString("since", sinceUnixEpoch + "");
        boolean hasMoreData = false;
        GraphRequest request = new GraphRequest(token, "/" + group.getId() + "/feed");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        int numberOfPostsRetrieved = 0;
        JSONObject root = null;
        JSONArray dataArray = null;
        do {
            root = response.getJSONObject();
            if (root != null) {

                //Check if our root contains a json array called 'data' that has all the group objects inside it

                if (root.has(FeedFields.DATA) && !root.isNull(FeedFields.DATA)) {

                    //retrieve our json array with group objects

                    dataArray = root.getJSONArray(GroupFields.DATA);

                    //For each iteration, we fetch the list of groups and append all of them to what we have so far

                    ArrayList<Post> posts = gson.fromJson(dataArray.toString(), typeToken.getType());
                    for (Post post : posts) {
                        if (numberOfPostsRetrieved < maximumNumberOfPostsToRetrieve) {
                            listPosts.add(post);
                            numberOfPostsRetrieved++;
                        }
                    }
                    request = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);

                    //We have more data if posts retrieved in the current iteration are not empty and we have a valid pagination request for the next round and we have not reached the limit or maximum number of posts to retrieve yet

                    hasMoreData = !posts.isEmpty() && request != null && numberOfPostsRetrieved != maximumNumberOfPostsToRetrieve;
                    if (hasMoreData) {
                        response = request.executeAndWait();
                    }
                }
            }
        }
        while (hasMoreData);
        return listPosts;
    }

    public static boolean requestDeletePost(AccessToken token, String postId) throws JSONException {
        GraphRequest graphRequest = new GraphRequest(token, postId, null, HttpMethod.DELETE);
        GraphResponse response = graphRequest.executeAndWait();
        JSONObject jsonObject = response.getJSONObject();
        return jsonObject != null && jsonObject.has("success") && !jsonObject.isNull("success") && jsonObject.getBoolean("success");
    }

    public static ArrayList<DeleteResponseInfo> requestDeletePosts(AccessToken token, ArrayList<Post> posts) throws JSONException {
        ArrayList<DeleteResponseInfo> responseInfos = new ArrayList<>();
        GraphRequestBatch requests = new GraphRequestBatch();
        for (Post post : posts) {
            GraphRequest graphRequest = new GraphRequest(token, post.getPostId(), null, HttpMethod.DELETE);
            requests.add(graphRequest);
        }
        List<GraphResponse> responses = requests.executeAndWait();
        for (int i = 0; i < responses.size(); i++) {
            GraphResponse response = responses.get(i);
            Post post = posts.get(i);
            JSONObject jsonObject = response.getJSONObject();
            boolean success = (jsonObject != null && jsonObject.has("success") && !jsonObject.isNull("success") && jsonObject.getBoolean("success"));
            DeleteResponseInfo responseInfo = new DeleteResponseInfo(success, post);
            responseInfos.add(responseInfo);
        }
        return responseInfos;
    }

    public static ArrayList<DeleteResponseInfo> requestDeletePosts(AccessToken token, RealmResults<Post> posts) throws JSONException, FacebookException {
        ArrayList<DeleteResponseInfo> responseInfos = new ArrayList<>();
        GraphRequestBatch requests = new GraphRequestBatch();
        for (Post post : posts) {
            GraphRequest graphRequest = new GraphRequest(token, post.getPostId(), null, HttpMethod.DELETE);
            requests.add(graphRequest);
        }
        List<GraphResponse> responses = requests.executeAndWait();
        for (int i = 0; i < responses.size(); i++) {
            GraphResponse response = responses.get(i);
            Post post = posts.get(i);
            JSONObject jsonObject = response.getJSONObject();
            boolean success = (jsonObject != null && jsonObject.has("success") && !jsonObject.isNull("success") && jsonObject.getBoolean("success"));
            DeleteResponseInfo responseInfo = new DeleteResponseInfo(success, post);
            responseInfos.add(responseInfo);
        }
        return responseInfos;
    }
}
