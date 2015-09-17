package slidenerd.vivz.fpam.model.json.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Picture implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
    @Expose
    private PictureData data;

    protected Picture(Parcel in) {
        data = (PictureData) in.readValue(PictureData.class.getClassLoader());
    }

    /**
     * @return The data
     */
    public PictureData getPictureData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setPictureData(PictureData data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }
}