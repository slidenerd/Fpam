package slidenerd.vivz.fpam.model.json.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is a container for the user's profile picture and returns profile pictures closest to a requested size maintaining its width, height, url and whether the user has set a custom profile picture on their facebook profile or facebook has provided the default image
 */

public class PictureData implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PictureData> CREATOR = new Parcelable.Creator<PictureData>() {
        @Override
        public PictureData createFromParcel(Parcel in) {
            return new PictureData(in);
        }

        @Override
        public PictureData[] newArray(int size) {
            return new PictureData[size];
        }
    };
    @Expose
    private int height;
    @SerializedName("is_silhouette")
    @Expose
    private boolean isSilhouette;
    @Expose
    private String url;
    @Expose
    private int width;

    protected PictureData(Parcel in) {
        height = in.readInt();
        isSilhouette = in.readByte() != 0x00;
        url = in.readString();
        width = in.readInt();
    }

    /**
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return The isSilhouette
     */
    public boolean isIsSilhouette() {
        return isSilhouette;
    }

    /**
     * @param isSilhouette The is_silhouette
     */
    public void setIsSilhouette(boolean isSilhouette) {
        this.isSilhouette = isSilhouette;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeByte((byte) (isSilhouette ? 0x01 : 0x00));
        dest.writeString(url);
        dest.writeInt(width);
    }
}