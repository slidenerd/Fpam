package slidenerd.vivz.fpam.model.json.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FBAdmin implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FBAdmin> CREATOR = new Parcelable.Creator<FBAdmin>() {
        @Override
        public FBAdmin createFromParcel(Parcel in) {
            return new FBAdmin(in);
        }

        @Override
        public FBAdmin[] newArray(int size) {
            return new FBAdmin[size];
        }
    };
    private static Gson gson = new Gson();
    //the profile picture of the admin
    @SerializedName("picture")
    private FBPicture picture;
    @SerializedName("id")
    private long id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("email")
    private String email;
    @SerializedName("last_name")
    private String lastName;

    public FBAdmin(long id, String firstName, String lastName, String email, FBPicture picture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
    }

    protected FBAdmin(Parcel in) {
        picture = (FBPicture) in.readValue(FBPicture.class.getClassLoader());
        id = in.readLong();
        firstName = in.readString();
        email = in.readString();
        lastName = in.readString();
    }

    public static String convertPictureToJson(FBPicture picture) {
        String pictureJson = gson.toJson(picture);
        return pictureJson;
    }

    public static FBPicture convertJsonToPicture(String pictureJson) {
        FBPicture picture = gson.fromJson(pictureJson, FBPicture.class);
        return picture;
    }

    public FBPicture getPicture() {
        return picture;
    }

    public void setPicture(FBPicture FBPicture) {
        this.picture = FBPicture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "FBAdmin [picture = " + picture + ", id = " + id + ", firstName = " + firstName + ", email = " + email + ", lastName = " + lastName + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(picture);
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(email);
        dest.writeString(lastName);
    }
}