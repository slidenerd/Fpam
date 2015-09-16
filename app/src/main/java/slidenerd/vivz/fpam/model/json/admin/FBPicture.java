package slidenerd.vivz.fpam.model.json.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the JSON tag data within which the profile picture of the user is contained.
 */
public class FBPicture implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FBPicture> CREATOR = new Parcelable.Creator<FBPicture>() {
        @Override
        public FBPicture createFromParcel(Parcel in) {
            return new FBPicture(in);
        }

        @Override
        public FBPicture[] newArray(int size) {
            return new FBPicture[size];
        }
    };
    @SerializedName("data")
    private FBData FBData;

    protected FBPicture(Parcel in) {
        FBData = (FBData) in.readValue(FBData.class.getClassLoader());
    }

    public FBData getData() {
        return FBData;
    }

    public void setData(FBData FBData) {
        this.FBData = FBData;
    }

    @Override
    public String toString() {
        return "FBPicture [FBData = " + FBData + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(FBData);
    }
}