package slidenerd.vivz.fpam.model.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.util.JSONUtils;

/**
 * Created by vivz on 29/09/15.
 */
public class GroupDeserializer implements JsonDeserializer<Group> {
    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject root = json.getAsJsonObject();
        final String name = root.get(JSONUtils.Groups.NAME).getAsString();
        final String id = root.get(JSONUtils.Groups.ID).getAsString();
        final String icon = root.get(JSONUtils.Groups.ICON).getAsString();
        final int unread = root.getAsJsonPrimitive(JSONUtils.Groups.UNREAD).getAsInt();
        final Group group = new Group();
        group.setId(id);
        group.setName(name);
        group.setIcon(icon);
        group.setUnread(unread);
        return group;
    }
}
