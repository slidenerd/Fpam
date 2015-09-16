package slidenerd.vivz.fpam.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import slidenerd.vivz.fpam.Keys;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.FBAdmin;
import slidenerd.vivz.fpam.model.json.feed.FBPost;
import slidenerd.vivz.fpam.model.json.group.FBGroup;

/**
 * Created by vivz on 29/07/15.
 */
public class FBUtils {
    public static boolean isValidToken(AccessToken accessToken) {
        return accessToken != null && !accessToken.isExpired();
    }

    public static Gson getGsonForRealm() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        return gson;
    }

    /**
     * Create an object of Gson to convert JSON String into a Java object. Specify the fields of the logged in user that you are interested to retrieve. Fire a Graph Request synchronously and get its JSON object.
     *
     * @param accessToken An access token needed to start session with Facebook
     * @return an admin object that contains all details such as name, email and the profile picture used by the admin.
     */
    @Nullable
    public static FBAdmin requestMeSync(AccessToken accessToken) {
        Gson gson = getGsonForRealm();
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,picture.type(normal).width(200).height(200)");
        GraphRequest request = new GraphRequest(accessToken, "me");
        request.setParameters(parameters);
        GraphResponse graphResponse = request.executeAndWait();
        JSONObject jsonObject = graphResponse.getJSONObject();
        FBAdmin admin = gson.fromJson(jsonObject.toString(), FBAdmin.class);
        return admin;
    }


    /**
     * @param accessToken An access token needed to start session with Facebook
     * @return a List containing all the groups owned by the logged in user and empty list if the logged in user doesn't own any groups or the groups were not retrieved for some reason. The JSON response is actually an object that contains an array with the name 'data' which contains all the groups.
     * @throws JSONException
     */
    public static ArrayList<FBGroup> requestGroupsSync(AccessToken accessToken) throws JSONException {
        Gson gson = getGsonForRealm();
        GraphRequest request = new GraphRequest(accessToken, "me/admined_groups");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,icon,unread");
        request.setParameters(parameters);
        GraphResponse response = request.executeAndWait();
        ArrayList<FBGroup> listGroups = new ArrayList<>();
        JSONObject jsonObject = response.getJSONObject();
        JSONArray arrayData = jsonObject.getJSONArray(Keys.JSON_KEY_DATA);
        for (int i = 0; i < arrayData.length(); i++) {
            try {
                JSONObject objectGroup = arrayData.getJSONObject(i);
                FBGroup group = gson.fromJson(objectGroup.toString(), FBGroup.class);
                listGroups.add(group);
            } catch (JSONException e) {
                L.m("" + e);
            }
        }
        return listGroups;
    }


    public static ArrayList<FBPost> requestFeedSync(AccessToken token, ArrayList<FBGroup> listGroups) throws JSONException {
        ArrayList<FBPost> listPosts = new ArrayList<>();
        GraphRequestBatch requestBatch = new GraphRequestBatch();
        Gson gson = getGsonForRealm();
        for (FBGroup group : listGroups) {
            Bundle parameters = new Bundle();
            parameters.putString("fields", "from,message,caption,comments{from,message},description,name,picture,type,updated_time,attachments{media,type,url},link");
            parameters.putString("limit", "15");
            GraphRequest request = new GraphRequest(token, "/" + group.getId() + "/feed");
            request.setParameters(parameters);
            requestBatch.add(request);
        }
        List<GraphResponse> listResponse = requestBatch.executeAndWait();
        for (GraphResponse response : listResponse) {
            JSONObject jsonObject = response.getJSONObject();
            JSONArray arrayData = jsonObject.getJSONArray(Keys.JSON_KEY_DATA);
            Type listType = new TypeToken<ArrayList<FBPost>>() {
            }.getType();
            L.m(arrayData.toString());
            listPosts = gson.fromJson(arrayData.toString(), listType);
//            for (int i = 0; i < arrayData.length(); i++) {
//                try {
//                    JSONObject objectFeed = arrayData.getJSONObject(i);
//                    FBPost post = gson.fromJson(objectFeed.toString(), FBPost.class);
//                    listPosts.add(post);
//                } catch (JSONException e) {
//                    L.m("" + e);
//                }
//            }
        }
        return listPosts;
    }
    // fields=from,message,link,created_time,updated_time,name,description,caption,status_type,picture,story,type&limit=25
}
