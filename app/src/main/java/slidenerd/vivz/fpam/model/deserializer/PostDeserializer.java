package slidenerd.vivz.fpam.model.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.util.DateUtils;
import slidenerd.vivz.fpam.util.ModelUtils;

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
import static slidenerd.vivz.fpam.extras.Fields.TYPE;
import static slidenerd.vivz.fpam.extras.Fields.UPDATED_TIME;
import static slidenerd.vivz.fpam.extras.Fields.URL;

/**
 * Created by vivz on 01/10/15.
 */
public class PostDeserializer implements JsonDeserializer<Post> {

    @Override
    public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        //Get the root JSON object

        JsonObject root = json.getAsJsonObject();
        Post post = new Post();

        //Retrieve 'id' of the post

        final String postId = root.get(ID).getAsString();

        final long rowId = ModelUtils.computePostItemId(postId);

        //Retrieve 'created_time' that contains time in the form of dd:MM:yyyy'T'hh:mm:ssZ

        final String createdTimeString = root.get(CREATED_TIME).getAsString();

        //Retrieve 'updated_time' that contains time in the form of dd:MM:yyyy'T'hh:mm:ssZ

        final String updatedTimeString = root.get(UPDATED_TIME).getAsString();

        //Retrieve 'type' of the post

        final String type = root.get(TYPE).getAsString();

        //Convert the time to milliseconds as per the local time zone and store them

        post.setPostId(postId);
        post.setRowId(rowId);
        post.setCreatedTime(DateUtils.getFBFormattedTime(createdTimeString));
        post.setUpdatedTime(DateUtils.getFBFormattedTime(updatedTimeString));
        post.setType(type);

        //Retrieve 'from' that contains user info of who made a post, it is optional in case the person doesnt exist on Facebook anymore

        final JsonObject fromObject = root.getAsJsonObject(FROM);
        if (fromObject != null) {

            //Retrieve 'id' from the 'from' object that corresponds to user id which is optional if the person doesn't exist on Facebook

            final JsonElement userIdElement = fromObject.get(ID);

            //Retrieve 'name' from the 'from' object that corresponds to user name which is optional if the person doesn't exist on Facebook

            final JsonElement userNameElement = fromObject.get(NAME);

            final JsonElement userPictureElement = fromObject.get(PICTURE);

            if (userIdElement != null) {
                final String userId = userIdElement.getAsString();
                post.setUserId(userId);
            }
            if (userNameElement != null) {
                final String userName = userNameElement.getAsString();
                post.setUserName(userName);
            }
            if (userPictureElement != null) {
                final JsonElement userPictureDataElement = userPictureElement.getAsJsonObject().get(DATA);
                if (userPictureDataElement != null) {
                    final JsonElement userPictureUrlElement = userPictureDataElement.getAsJsonObject().get(URL);
                    if (userPictureUrlElement != null) {
                        final String userPicture = userPictureUrlElement.getAsString();
                        post.setUserPicture(userPicture);
                    }
                }
            }
        }

        //Retrieve 'message' from the root JSON object which is optional

        final JsonElement messageElement = root.get(MESSAGE);
        if (messageElement != null) {
            final String message = messageElement.getAsString();
            post.setMessage(message);
        }

        //Retrieve 'name' from the root JSONObject which is optional and contains the name of a link as part of name, caption, description when a user posts links

        final JsonElement nameElement = root.get(NAME);
        if (nameElement != null) {
            final String name = nameElement.getAsString();
            post.setName(name);
        }

        //Retrieve 'caption' from the root JSONObject which is optional and contains the caption of a link as part of name, caption, description when a user posts links

        final JsonElement captionElement = root.get(CAPTION);
        if (captionElement != null) {
            final String caption = captionElement.getAsString();
            post.setCaption(caption);
        }

        //Retrieve 'description' from the root JSONObject which is optional and contains the description of a link as part of name, caption, description when a user posts links

        final JsonElement descriptionElement = root.get(DESCRIPTION);
        if (descriptionElement != null) {
            final String description = descriptionElement.getAsString();
            post.setDescription(description);
        }

        //Retrieve 'picture' from the root JSONObject which is optional and contains a url of an image if used in the post

        final JsonElement pictureElement = root.get(FULL_PICTURE);
        if (pictureElement != null) {
            final String picture = pictureElement.getAsString();
            post.setPicture(picture);
        }

        //Retrieve 'link' from the root JSONObject which is optional and contains a link if used in the post

        final JsonElement linkElement = root.get(LINK);
        if (linkElement != null) {
            final String link = linkElement.getAsString();
            post.setLink(link);
        }
        return post;
    }
}
