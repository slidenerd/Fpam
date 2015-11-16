package slidenerd.vivz.fpam.model.pojo;

import slidenerd.vivz.fpam.model.json.Post;

/**
 * Created by vivz on 28/10/15.
 */
public class DeleteRequestInfo {
    private int position;
    private Post post;

    public DeleteRequestInfo(int position, Post post) {
        this.position = position;
        this.post = post;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
