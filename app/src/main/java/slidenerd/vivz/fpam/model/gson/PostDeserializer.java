package slidenerd.vivz.fpam.model.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.util.JSONUtils.FeedFields;

/**
 * TODO Add appropriate comments
 * Created by vivz on 01/10/15.
 */
public class PostDeserializer implements JsonDeserializer<Post> {


    @Override
    public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        Post post = new Post();
        final String postId = root.get(FeedFields.ID).getAsString();
        final String createdTime = root.get(FeedFields.CREATED_TIME).getAsString();
        final String updatedTime = root.get(FeedFields.UPDATED_TIME).getAsString();
        final String type = root.get(FeedFields.TYPE).getAsString();

        post.setPostId(postId);
        post.setCreatedTime(createdTime);
        post.setUpdatedTime(updatedTime);
        post.setType(type);

        final JsonObject fromObject = root.getAsJsonObject(FeedFields.FROM);
        final String userId = fromObject.get(FeedFields.ID).getAsString();
        final String userName = fromObject.get(FeedFields.NAME).getAsString();
        post.setUserId(userId);
        post.setUserName(userName);

        final JsonElement messageElement = root.get(FeedFields.MESSAGE);
        if (messageElement != null) {
            final String message = messageElement.getAsString();
            post.setMessage(message);
        }

        final JsonElement nameElement = root.get(FeedFields.NAME);
        if (nameElement != null) {
            final String name = nameElement.getAsString();
            post.setName(name);
        }

        final JsonElement captionElement = root.get(FeedFields.CAPTION);
        if (captionElement != null) {
            final String caption = captionElement.getAsString();
            post.setCaption(caption);
        }

        final JsonElement descriptionElement = root.get(FeedFields.DESCRIPTION);
        if (descriptionElement != null) {
            final String description = descriptionElement.getAsString();
            post.setDescription(description);
        }

        final JsonElement pictureElement = root.get(FeedFields.PICTURE);
        if (pictureElement != null) {
            final String picture = pictureElement.getAsString();
            post.setPicture(picture);
        }

        final JsonElement linkElement = root.get(FeedFields.LINK);
        if (linkElement != null) {
            final String link = linkElement.getAsString();
            post.setLink(link);
        }
        return post;
    }
}
