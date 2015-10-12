package slidenerd.vivz.fpam.model.pojo;

import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 11/10/15.
 */
public class DeleteResponseInfo {
    private boolean status;
    private Post post;

    public DeleteResponseInfo(boolean status, Post post) {
        this.status = status;
        this.post = post;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
