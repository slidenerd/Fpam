package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Attachment implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };
    @Expose
    private AttachmentMedia media;
    @Expose
    private String type;
    @Expose
    private String url;

    protected Attachment(Parcel in) {
        media = (AttachmentMedia) in.readValue(AttachmentMedia.class.getClassLoader());
        type = in.readString();
        url = in.readString();
    }

    /**
     * @return The media
     */
    public AttachmentMedia getAttachmentMedia() {
        return media;
    }

    /**
     * @param media The media
     */
    public void setAttachmentMedia(AttachmentMedia media) {
        this.media = media;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(media);
        dest.writeString(type);
        dest.writeString(url);
    }
}