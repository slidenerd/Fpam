package slidenerd.vivz.fpam.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import slidenerd.vivz.fpam.model.json.feed.FBPost;

/**
 * Created by vivz on 08/09/15.
 */
public class FBPostTypeAdapter extends TypeAdapter<FBPost> {
    @Override
    public void write(JsonWriter out, FBPost value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        FBPost fileType = (FBPost) value;
        // Here write what you want to the JsonWriter.
        out.beginObject();
        out.name("id");
        out.value(fileType.getId());
        out.name("message");
        out.value(fileType.getMessage());
        out.endObject();
    }

    @Override
    public FBPost read(JsonReader in) throws IOException {


        FBPost post = new FBPost();
        in.beginObject();
        while (in.hasNext()) {
            String nodeName = in.nextName();
            if (nodeName.equals("from")) {
                in.beginObject();
                while (in.hasNext()) {
                    String fromNodeName = in.nextName();
                    if (fromNodeName.equals("name")) {
                        String personName = in.nextString();
                    }
                    if (fromNodeName.equals("id")) {
                        String personId = in.nextString();
                    }
                }
                in.endObject();
            }

        }
        in.endObject();
        return post;
    }
}
