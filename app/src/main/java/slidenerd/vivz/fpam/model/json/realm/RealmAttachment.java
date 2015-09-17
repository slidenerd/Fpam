package slidenerd.vivz.fpam.model.json.realm;


import io.realm.RealmObject;

/**
 * Created by vivz on 17/09/15.
 */
public class RealmAttachment extends RealmObject {

    private String type;
    private String url;
    private int height;
    private String src;
    private int width;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
