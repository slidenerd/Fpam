package slidenerd.vivz.fpam.model.json.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Paging implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Paging> CREATOR = new Parcelable.Creator<Paging>() {
        @Override
        public Paging createFromParcel(Parcel in) {
            return new Paging(in);
        }

        @Override
        public Paging[] newArray(int size) {
            return new Paging[size];
        }
    };
    @Expose
    private String previous;
    @Expose
    private String next;

    protected Paging(Parcel in) {
        previous = in.readString();
        next = in.readString();
    }

    /**
     * @return The previous
     */
    public String getPrevious() {
        return previous;
    }

    /**
     * @param previous The previous
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    /**
     * @return The next
     */
    public String getNext() {
        return next;
    }

    /**
     * @param next The next
     */
    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(previous);
        dest.writeString(next);
    }
}