package MastodonTypes;

/*
  Created by gsora on 4/21/17.
  <p>
  A Tag present in a Mastodon Status object.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;

@SuppressWarnings("unused")
public class Tag extends RealmObject {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}