package slidenerd.vivz.fpam.model.json.feed;

import java.io.Serializable;

public class FBAttachmentMedia implements Serializable{
    private FBAttachmentImage image;

    public FBAttachmentImage getFBAttachmentImage() {
        return image;
    }

    public void setFBAttachmentImage(FBAttachmentImage image) {
        this.image = image;
    }
}