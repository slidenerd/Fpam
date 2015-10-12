package slidenerd.vivz.fpam.model.pojo;

import slidenerd.vivz.fpam.model.json.feed.Post;

/**
 * Created by vivz on 11/10/15.
 */
public class DeleteResponseInfo {
    private boolean success;
    private Post post;

    public DeleteResponseInfo(boolean success, Post post) {
        this.success = success;
        this.post = post;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
