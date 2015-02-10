package co.mobilemakers.githubrepos;

/**
 * Created by diana.perez on 10/02/2015.
 */
public class TagPopularity {

    private String tagName;
    private String popularity;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return tagName + ": " + popularity;
    }
}
