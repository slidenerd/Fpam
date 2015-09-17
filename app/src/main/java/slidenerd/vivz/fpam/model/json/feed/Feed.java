package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Feed implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };
    @Expose
    private List<Post> data = new ArrayList<Post>();
    @Expose
    private Paging paging;

    protected Feed(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Post>();
            in.readList(data, Post.class.getClassLoader());
        } else {
            data = null;
        }
        paging = (Paging) in.readValue(Paging.class.getClassLoader());
    }

    /**
     * @return The data
     */
    public List<Post> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Post> data) {
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