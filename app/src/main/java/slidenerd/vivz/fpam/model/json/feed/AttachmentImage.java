package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class AttachmentImage implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AttachmentImage> CREATOR = new Parcelable.Creator<AttachmentImage>() {
        @Override
        public AttachmentImage createFromParcel(Parcel in) {
            return new AttachmentImage(in);
        }

        @Override
        public AttachmentImage[] newArray(int size) {
            return new AttachmentImage[size];
        }
    };
    @Expose
    private int height;
    @Expose
    private String src;
    @Expose
    private int width;

    protected AttachmentImage(Parcel in) {
        height = in.readInt();
        src = in.readString();
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
     * @return The src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src The src
     */
    public void setSrc(String src) {
        this.src = src;
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
        dest.writeString(src);
        dest.writeInt(width);
    }
}