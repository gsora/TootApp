
package MastodonTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@SuppressWarnings("unused")
public class MediaAttachment extends RealmObject implements Serializable {

    private final static long serialVersionUID = 8349948183094094758L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("remote_url")
    @Expose
    private String remoteUrl;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("preview_url")
    @Expose
    private String previewUrl;
    @SerializedName("text_url")
    @Expose
    private String textUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public void setTextUrl(String textUrl) {
        this.textUrl = textUrl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(remoteUrl).append(type).append(url).append(previewUrl).append(textUrl).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MediaAttachment)) {
            return false;
        }
        MediaAttachment rhs = ((MediaAttachment) other);
        return new EqualsBuilder().append(id, rhs.id).append(remoteUrl, rhs.remoteUrl).append(type, rhs.type).append(url, rhs.url).append(previewUrl, rhs.previewUrl).append(textUrl, rhs.textUrl).isEquals();
    }

}
