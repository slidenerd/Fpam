package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class CommentPaging implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentPaging> CREATOR = new Parcelable.Creator<CommentPaging>() {
        @Override
        public CommentPaging createFromParcel(Parcel in) {
            return new CommentPaging(in);
        }

        @Override
        public CommentPaging[] newArray(int size) {
            return new CommentPaging[size];
        }
    };
    @Expose
    private Cursors cursors;

    protected CommentPaging(Parcel in) {
        cursors = (Cursors) in.readValue(Cursors.class.getClassLoader());
    }

    /**
     * @return The cursors
     */
    public Cursors getCursors() {
        return cursors;
    }

    /**
     * @param cursors The cursors
     */
    public void setCursors(Cursors cursors) {
        this.cursors = cursors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cursors);
    }
}