package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;


public class AttachmentMedia implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AttachmentMedia> CREATOR = new Parcelable.Creator<AttachmentMedia>() {
        @Override
        public AttachmentMedia createFromParcel(Parcel in) {
            return new AttachmentMedia(in);
        }

        @Override
        public AttachmentMedia[] newArray(int size) {
            return new AttachmentMedia[size];
        }
    };
    @Expose
    private AttachmentImage image;

    protected AttachmentMedia(Parcel in) {
        image = (AttachmentImage) in.readValue(AttachmentImage.class.getClassLoader());
    }

    /**
     * @return The image
     */
    public AttachmentImage getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(AttachmentImage image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(image);
    }
}