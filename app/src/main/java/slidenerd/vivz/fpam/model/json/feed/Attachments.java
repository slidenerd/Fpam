package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Attachments implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Attachments> CREATOR = new Parcelable.Creator<Attachments>() {
        @Override
        public Attachments createFromParcel(Parcel in) {
            return new Attachments(in);
        }

        @Override
        public Attachments[] newArray(int size) {
            return new Attachments[size];
        }
    };
    @Expose
    private List<Attachment> data = new ArrayList<Attachment>();

    protected Attachments(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Attachment>();
            in.readList(data, Attachment.class.getClassLoader());
        } else {
            data = null;
        }
    }

    /**
     * @return The data
     */
    public List<Attachment> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Attachment> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }
}