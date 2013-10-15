package earth.xor;

public class Bookmark {

    private String objectId;
    private String title;
    private String url;

    public void setId(String id) {
        this.objectId = id;
    }

    public String getId() {
        return this.objectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    @Override
    public String toString() {
        return "Bookmark with Title:" + getTitle() + " and URL: " + getUrl()
                + " @" + Integer.toHexString(hashCode());
    }
}
