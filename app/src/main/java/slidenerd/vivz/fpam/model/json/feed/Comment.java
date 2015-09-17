package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
    @Expose
    private User from;
    @Expose
    private String message;
    @Expose
    private String id;
    @SerializedName("created_time")
    @Expose
    private String createdTime;

    protected Comment(Parcel in) {
        from = (User) in.readValue(User.class.getClassLoader());
        message = in.readString();
        id = in.readString();
        createdTime = in.readString();
    }

    /**
     * @return The from
     */
    public User getFrom() {
        return from;
    }

    /**
     * @param from The from
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(from);
        dest.writeString(message);
        dest.writeString(id);
        dest.writeString(createdTime);
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}