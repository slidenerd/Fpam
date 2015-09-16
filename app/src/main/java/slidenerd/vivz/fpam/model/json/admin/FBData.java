package slidenerd.vivz.fpam.model.json.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * This class is a container for the user's profile picture and returns profile pictures closest to a requested size maintaining its width, height, url and whether the user has set a custom profile picture on their facebook profile or facebook has provided the default image
 */
public class FBData implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FBData> CREATOR = new Parcelable.Creator<FBData>() {
        @Override
        public FBData createFromParcel(Parcel in) {
            return new FBData(in);
        }

        @Override
        public FBData[] newArray(int size) {
            return new FBData[size];
        }
    };
    //Whether the profile picture has been set (i.e. the profile picture is the default picture) false = user has set a custom profile picture, true = user has not set the profile picture and facebook has applied the default picture
    @SerializedName("is_silhouette")
    private boolean isSilhouette;
    //Height of the requested profile picture, in pixels
    @SerializedName("height")
    private int height;
    //Width of the requested profile picture, in pixels
    @SerializedName("width")
    private int width;
    //URL pointing to the returned picture
    @SerializedName("url")
    private String url;

    protected FBData(Parcel in) {
        isSilhouette = in.readByte() != 0x00;
        height = in.readInt();
        width = in.readInt();
        url = in.readString();
    }

    public boolean getIsSilhouette() {
        return isSilhouette;
    }

    public void setIsSilhouette(boolean isSilhouette) {
        this.isSilhouette = isSilhouette;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FBData [isSilhouette = " + isSilhouette + ", height = " + height + ", width = " + width + ", url = " + url + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSilhouette ? 0x01 : 0x00));
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeString(url);
    }
}