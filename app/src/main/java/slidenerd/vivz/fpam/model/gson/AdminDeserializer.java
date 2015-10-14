package slidenerd.vivz.fpam.model.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.extras.AdminFields;
import slidenerd.vivz.fpam.model.json.admin.Admin;

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

        final String id = root.get(AdminFields.ID).getAsString();

        //Retrieve 'email'

        final String email = root.get(AdminFields.EMAIL).getAsString();

        //Retrieve 'name'

        final String name = root.get(AdminFields.NAME).getAsString();

        //Retrieve Json object 'picture' inside root json object

        final JsonObject pictureObject = root.getAsJsonObject(AdminFields.PICTURE);

        //Retrieve Json object 'data' inside 'picture' json object

        final JsonObject dataObject = pictureObject.getAsJsonObject(AdminFields.DATA);

        //Retrieve 'width' inside 'data'

        final int width = dataObject.getAsJsonPrimitive(AdminFields.WIDTH).getAsInt();

        //Retrieve 'height' inside 'data'

        final int height = dataObject.getAsJsonPrimitive(AdminFields.HEIGHT).getAsInt();

        //Retrieve 'is_silhouette' inside 'data'

        final boolean isSilhouette = dataObject.getAsJsonPrimitive(AdminFields.IS_SILHOUETTE).getAsBoolean();

        //Retrieve 'url' inside 'data'

        final String url = dataObject.getAsJsonPrimitive(AdminFields.URL).getAsString();

        //Set appropriate fields

        admin.setId(id);
        admin.setEmail(email);
        admin.setName(name);
        admin.setWidth(width);
        admin.setHeight(height);
        admin.setIsSilhouette(isSilhouette);
        admin.setUrl(url);
        return admin;
    }
}
