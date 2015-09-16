package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FBComments implements Serializable {
    @SerializedName("data")
    private ArrayList<FBComment> data = new ArrayList<>();

    public ArrayList<FBComment> getData() {
        return data;
    }

    public void setData(ArrayList<FBComment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (FBComment comment : data) {
            builder.append(comment.toString() + "\n");
        }
        return builder.toString();
    }
}