package xyz.gsora.toot;

/**
 * Created by gsora on 4/20/17.
 * <p>
 * A class to represent an Username/Instance URI tuple.
 */
public class UserTuple {
    private String user;
    private String instanceURI;

    public UserTuple() {
        this.user = "";
        this.instanceURI = "";
    }

    public UserTuple(String user, String instanceURI) {
        this.user = user;
        this.instanceURI = instanceURI;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInstanceURI() {
        return instanceURI;
    }

    public void setInstanceURI(String instanceURI) {
        this.instanceURI = instanceURI;
    }

    @Override
    public String toString() {
        return "UserTuple{" +
                "user='" + user + '\'' +
                ", instanceURI='" + instanceURI + '\'' +
                '}';
    }
}
