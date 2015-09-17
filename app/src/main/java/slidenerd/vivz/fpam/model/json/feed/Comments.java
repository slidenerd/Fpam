package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Comments implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Comments> CREATOR = new Parcelable.Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel in) {
            return new Comments(in);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };
    @Expose
    private List<Comment> data = new ArrayList<Comment>();
    @Expose
    private CommentPaging paging;

    protected Comments(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Comment>();
            in.readList(data, Comment.class.getClassLoader());
        } else {
            data = null;
        }
        paging = (CommentPaging) in.readValue(CommentPaging.class.getClassLoader());
    }

    /**
     * @return The data
     */
    public List<Comment> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Comment> data) {
        this.data = data;
    }

    /**
     * @return The paging
     */
    public CommentPaging getCommentPaging() {
        return paging;
    }

    /**
     * @param paging The paging
     */
    public void setCommentPaging(CommentPaging paging) {
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