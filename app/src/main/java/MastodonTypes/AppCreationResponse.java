package MastodonTypes;

/*
 * Created by gsora on 4/20/17.
 *
 * This class represents what the Mastodon API gives us after a succesful POST request to /api/v1/apps.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class AppCreationResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("client_secret")
    @Expose
    private String clientSecret;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
