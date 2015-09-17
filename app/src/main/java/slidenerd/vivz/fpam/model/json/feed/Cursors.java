package slidenerd.vivz.fpam.model.json.feed;


import com.google.gson.annotations.Expose;

public class Cursors {

    @Expose
    private String after;
    @Expose
    private String before;

    /**
     * @return The after
     */
    public String getAfter() {
        return after;
    }

    /**
     * @param after The after
     */
    public void setAfter(String after) {
        this.after = after;
    }

    /**
     * @return The before
     */
    public String getBefore() {
        return before;
    }

    /**
     * @param before The before
     */
    public void setBefore(String before) {
        this.before = before;
    }

}
