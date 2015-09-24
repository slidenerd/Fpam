package slidenerd.vivz.fpam.model.json.admin;

import org.parceler.Parcel;

import io.realm.PictureDataRealmProxy;
import io.realm.RealmObject;

/**
 * TODO this will crash the app if you commit now
 * This class is a container for the user's profile picture and returns profile pictures closest to a requested size maintaining its width, height, url and whether the user has set a custom profile picture on their facebook profile or facebook has provided the default image
 */

@Parcel(implementations = {PictureDataRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {PictureData.class})
public class PictureData extends RealmObject {

    private int height;
    private boolean is_silhouette;
    private String url;
    private int width;

    public PictureData() {
    }

    public PictureData(String url, int width, int height, boolean is_silhouette) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.is_silhouette = is_silhouette;
    }


    /**
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(int height) {
        this.height = height;
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

    /**
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public boolean is_silhouette() {
        return is_silhouette;
    }

    public void setIs_silhouette(boolean is_silhouette) {
        this.is_silhouette = is_silhouette;
    }
}