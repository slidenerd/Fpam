package slidenerd.vivz.fpam.model.json.group;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import slidenerd.vivz.fpam.model.json.feed.Paging;

public class Groups implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Groups> CREATOR = new Parcelable.Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };
    @Expose
    private List<Group> data = new ArrayList<Group>();
    @Expose
    private Paging paging;

    protected Groups(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Group>();
            in.readList(data, Group.class.getClassLoader());
        } else {
            data = null;
        }
        paging = (Paging) in.readValue(Paging.class.getClassLoader());
    }

    /**
     * @return The data
     */
    public List<Group> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Group> data) {
        this.data = data;
    }

    /**
     * @return The paging
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * @param paging The paging
     */
    public void setPaging(Paging paging) {
        this.paging = paging;
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
        dest.writeValue(paging);
    }
}