package slidenerd.vivz.fpam.model.json.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FBComment implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("message")
    private String message;

    @SerializedName("from")
    private FBUser from;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FBUser getFrom() {
        return from;
    }

    public void setFrom(FBUser from) {
        this.from = from;
    }

}
