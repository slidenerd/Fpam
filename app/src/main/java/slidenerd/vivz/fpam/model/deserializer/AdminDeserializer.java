package slidenerd.vivz.fpam.model.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.model.json.admin.Admin;

import static slidenerd.vivz.fpam.extras.Fields.DATA;
import static slidenerd.vivz.fpam.extras.Fields.EMAIL;
import static slidenerd.vivz.fpam.extras.Fields.HEIGHT;
import static slidenerd.vivz.fpam.extras.Fields.ID;
import static slidenerd.vivz.fpam.extras.Fields.IS_SILHOUETTE;
import static slidenerd.vivz.fpam.extras.Fields.NAME;
import static slidenerd.vivz.fpam.extras.Fields.PICTURE;
import static slidenerd.vivz.fpam.extras.Fields.URL;
import static slidenerd.vivz.fpam.extras.Fields.WIDTH;

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

        final String id = root.get(ID).getAsString();

        //Retrieve 'email'

        final String email = root.get(EMAIL).getAsString();

        //Retrieve 'name'

        final String name = root.get(NAME).getAsString();

        //Retrieve Json object 'picture' inside root json object

        final JsonObject pictureObject = root.getAsJsonObject(PICTURE);

        //Retrieve Json object 'data' inside 'picture' json object

        final JsonObject dataObject = pictureObject.getAsJsonObject(DATA);

        //Retrieve 'width' inside 'data'

        final int width = dataObject.getAsJsonPrimitive(WIDTH).getAsInt();

        //Retrieve 'height' inside 'data'

        final int height = dataObject.getAsJsonPrimitive(HEIGHT).getAsInt();

        //Retrieve 'is_silhouette' inside 'data'

        final boolean isSilhouette = dataObject.getAsJsonPrimitive(IS_SILHOUETTE).getAsBoolean();

        //Retrieve 'url' inside 'data'

        final String url = dataObject.getAsJsonPrimitive(URL).getAsString();

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
