package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FBAttachments implements Serializable {
    @SerializedName("data")
    private ArrayList<FBAttachment> data = new ArrayList<>();

    public ArrayList<FBAttachment> getFBAttachment() {
        return data;
    }

    public void setFBAttachment(ArrayList<FBAttachment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (FBAttachment attachment : data) {
            builder.append(attachment.toString() + "\n");
        }
        return builder.toString();
    }
}