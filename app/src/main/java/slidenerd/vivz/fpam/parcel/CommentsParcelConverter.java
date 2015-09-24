package slidenerd.vivz.fpam.parcel;

import android.os.Parcel;

import org.parceler.Parcels;

import slidenerd.vivz.fpam.model.json.feed.Comment;

/**
 * Created by vivz on 21/09/15.
 */
public class CommentsParcelConverter extends RealmListParcelConverter<Comment> {
    @Override
    public void itemToParcel(Comment input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(Comment.class, input), 0);
    }

    @Override
    public Comment itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Comment.class.getClassLoader()));
    }
}
