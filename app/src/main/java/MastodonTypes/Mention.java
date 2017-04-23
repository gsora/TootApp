package MastodonTypes;

/**
 * Created by gsora on 4/21/17.
 * <p>
 * A Mention present in a Mastodon Status object.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;

public class Mention extends RealmObject {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("acct")
    @Expose
    private String acct;
    @SerializedName("id")
    @Expose
    private String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}