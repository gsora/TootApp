package xyz.gsora.toot.Mastodon;

@SuppressWarnings("unused")
public class Link {

    private String URL;
    private LinkParser.Relationship rel;

    public Link(String URL, LinkParser.Relationship rel) {
        this.URL = URL;
        this.rel = rel;
    }

    public Link() {
    }

    public LinkParser.Relationship getRel() {
        return rel;
    }

    public void setRel(LinkParser.Relationship rel) {
        this.rel = rel;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Link{" +
                "URL='" + URL + '\'' +
                ", rel=" + rel +
                '}';
    }
}
