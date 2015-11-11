package slidenerd.vivz.fpam.model.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.model.json.group.Group;

import static slidenerd.vivz.fpam.extras.Fields.ICON;
import static slidenerd.vivz.fpam.extras.Fields.ID;
import static slidenerd.vivz.fpam.extras.Fields.NAME;
import static slidenerd.vivz.fpam.extras.Fields.UNREAD;

/**
 * Created by vivz on 29/09/15.
 */
public class GroupDeserializer implements JsonDeserializer<Group> {
    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        //Get the root JSON object

        final JsonObject root = json.getAsJsonObject();

        //Get the 'name'

        final String name = root.get(NAME).getAsString();

        //Get the 'id'

        final String id = root.get(ID).getAsString();

        //Get the 'icon'

        final String icon = root.get(ICON).getAsString();

        //Get the 'unread'

        final int unread = root.getAsJsonPrimitive(UNREAD).getAsInt();
        final Group group = new Group();
        group.setGroupId(id);
        group.setGroupName(name);
        group.setGroupIcon(icon);
        group.setUnread(unread);
        return group;
    }
}
