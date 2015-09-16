package slidenerd.vivz.fpam.model.json.feed;

import java.io.Serializable;

public class FBAttachmentImage implements Serializable{
    private String height;

    private String width;

    private String src;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return "FBAttachmentImage [height = " + height + ", width = " + width + ", src = " + src + "]";
    }
}