package slidenerd.vivz.fpam.model.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.util.JSONUtils;

/**
 * Created by vivz on 29/09/15.
 */
public class AdminDeserializer implements JsonDeserializer<Admin> {
    @Override
    public Admin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Get the root JSON object
        final JsonObject root = json.getAsJsonObject();
        final Admin admin = new Admin();
        //Retrieve 'id'
        final String id = root.get(JSONUtils.AdminFields.ID).getAsString();
        //Retrieve 'email'
        final String email = root.get(JSONUtils.AdminFields.EMAIL).getAsString();
        //Retrieve 'first_name'
        final String firstName = root.get(JSONUtils.AdminFields.FIRST_NAME).getAsString();
        //Retrieve 'last_name'
        final String lastName = root.get(JSONUtils.AdminFields.LAST_NAME).getAsString();
        //Retrieve Json object 'picture' inside root json object
        final JsonObject pictureObject = root.getAsJsonObject(JSONUtils.AdminFields.PICTURE);
        //Retrieve Json object 'data' inside 'picture' json object
        final JsonObject dataObject = pictureObject.getAsJsonObject(JSONUtils.AdminFields.DATA);
        //Retrieve 'width' inside 'data'
        final int width = dataObject.getAsJsonPrimitive(JSONUtils.AdminFields.WIDTH).getAsInt();
        //Retrieve 'height' inside 'data'
        final int height = dataObject.getAsJsonPrimitive(JSONUtils.AdminFields.HEIGHT).getAsInt();
        //Retrieve 'is_silhouette' inside 'data'
        final boolean isSilhouette = dataObject.getAsJsonPrimitive(JSONUtils.AdminFields.IS_SILHOUETTE).getAsBoolean();
        //Retrieve 'url' inside 'data'
        final String url = dataObject.getAsJsonPrimitive(JSONUtils.AdminFields.URL).getAsString();
        //Set appropriate fields
        admin.setId(id);
        admin.setEmail(email);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setWidth(width);
        admin.setHeight(height);
        admin.setIsSilhouette(isSilhouette);
        admin.setUrl(url);
        return admin;
    }
}
