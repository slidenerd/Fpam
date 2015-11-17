package slidenerd.vivz.fpam.model.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import slidenerd.vivz.fpam.L;
import slidenerd.vivz.fpam.extras.Constants;
import slidenerd.vivz.fpam.model.json.Post;

import static slidenerd.vivz.fpam.extras.Fields.CAPTION;
import static slidenerd.vivz.fpam.extras.Fields.CREATED_TIME;
import static slidenerd.vivz.fpam.extras.Fields.DATA;
import static slidenerd.vivz.fpam.extras.Fields.DESCRIPTION;
import static slidenerd.vivz.fpam.extras.Fields.FROM;
import static slidenerd.vivz.fpam.extras.Fields.FULL_PICTURE;
import static slidenerd.vivz.fpam.extras.Fields.ID;
import static slidenerd.vivz.fpam.extras.Fields.LINK;
import static slidenerd.vivz.fpam.extras.Fields.MESSAGE;
import static slidenerd.vivz.fpam.extras.Fields.NAME;
import static slidenerd.vivz.fpam.extras.Fields.PICTURE;
import static slidenerd.vivz.fpam.extras.Fields.TO;
import static slidenerd.vivz.fpam.extras.Fields.TYPE;
import static slidenerd.vivz.fpam.extras.Fields.UPDATED_TIME;
import static slidenerd.vivz.fpam.extras.Fields.URL;

/**
 * Created by vivz on 01/10/15.
 */
public class PostDeserializer implements JsonDeserializer<Post> {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static final long formatFacebookTime(String timeString) {

        long timeMillis = Constants.NA;
        try {
            timeMillis = format.parse(timeString).getTime();
        } catch (ParseException e) {
            L.m(timeString + " " + e);
        }
        return timeMillis;
    }

    @Override
    public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        //Get the root JSON object

        JsonObject root = json.getAsJsonObject();
        Post post = new Post();

        //Retrieve 'id' of the post

        final String postId = root.get(ID).getAsString();

        //Retrieve 'created_time' that contains time in the form of dd:MM:yyyy'T'hh:mm:ssZ

        final String createdTime = root.get(CREATED_TIME).getAsString();

        //Retrieve 'updated_time' that contains time in the form of dd:MM:yyyy'T'hh:mm:ssZ

        final String updatedTime = root.get(UPDATED_TIME).getAsString();

        //Retrieve 'type' of the post

        final String type = root.get(TYPE).getAsString();
        post.setPostId(postId);
        post.setRowId(Post.computeRowId(postId));

        //Convert the time to milliseconds as per the local time zone and store them
        post.setCreatedTime(formatFacebookTime(createdTime));
        post.setUpdatedTime(formatFacebookTime(updatedTime));
        post.setType(type);


        final JsonObject to = root.getAsJsonObject(TO);
        if (to != null) {
            final JsonArray data = to.get(DATA).getAsJsonArray();
            if (data != null) {
                final JsonObject first = data.get(0).getAsJsonObject();
                if (first != null) {
                    final String groupId = first.get(ID).getAsString();
                    post.setGroupId(groupId);
                }
            }
        }



        //Retrieve 'from' that contains user info of who made a post, it is optional in case the person doesnt exist on Facebook anymore

        final JsonObject from = root.getAsJsonObject(FROM);
        if (from != null) {

            //Retrieve 'id' from the 'from' object that corresponds to user id which is optional if the person doesn't exist on Facebook

            final JsonElement userId = from.get(ID);

            //Retrieve 'name' from the 'from' object that corresponds to user name which is optional if the person doesn't exist on Facebook

            final JsonElement userName = from.get(NAME);

            final JsonElement userPicture = from.get(PICTURE);

            if (userId != null) {
                post.setUserId(userId.getAsString());
            }
            if (userName != null) {
                post.setUserName(userName.getAsString());
            }
            if (userPicture != null) {
                final JsonElement data = userPicture.getAsJsonObject().get(DATA);
                if (data != null) {
                    final JsonElement url = data.getAsJsonObject().get(URL);
                    if (url != null) {
                        post.setUserPicture(url.getAsString());
                    }
                }
            }
        }

        //Retrieve 'message' from the root JSON object which is optional

        final JsonElement message = root.get(MESSAGE);
        if (message != null) {
            post.setMessage(message.getAsString());
        }

        //Retrieve 'name' from the root JSONObject which is optional and contains the name of a link as part of name, caption, description when a user posts links

        final JsonElement name = root.get(NAME);
        if (name != null) {
            post.setName(name.getAsString());
        }

        //Retrieve 'caption' from the root JSONObject which is optional and contains the caption of a link as part of name, caption, description when a user posts links

        final JsonElement caption = root.get(CAPTION);
        if (caption != null) {
            post.setCaption(caption.getAsString());
        }

        //Retrieve 'description' from the root JSONObject which is optional and contains the description of a link as part of name, caption, description when a user posts links

        final JsonElement description = root.get(DESCRIPTION);
        if (description != null) {
            post.setDescription(description.getAsString());
        }

        //Retrieve 'picture' from the root JSONObject which is optional and contains a url of an image if used in the post

        final JsonElement picture = root.get(FULL_PICTURE);
        if (picture != null) {
            post.setPicture(picture.getAsString());
        }

        //Retrieve 'link' from the root JSONObject which is optional and contains a link if used in the post

        final JsonElement link = root.get(LINK);
        if (link != null) {
            post.setLink(link.getAsString());
        }
        return post;
    }
}
