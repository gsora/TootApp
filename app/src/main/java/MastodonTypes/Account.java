
package MastodonTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Account extends RealmObject implements Serializable {

    private final static long serialVersionUID = -7201177137522198661L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("acct")
    @Expose
    private String acct;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("locked")
    @Expose
    private Boolean locked;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("header")
    @Expose
    private String header;
    @SerializedName("followers_count")
    @Expose
    private Integer followersCount;
    @SerializedName("following_count")
    @Expose
    private Integer followingCount;
    @SerializedName("statuses_count")
    @Expose
    private Integer statusesCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(Integer statusesCount) {
        this.statusesCount = statusesCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(username).append(acct).append(displayName).append(locked).append(createdAt).append(note).append(url).append(avatar).append(header).append(followersCount).append(followingCount).append(statusesCount).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Account)) {
            return false;
        }
        Account rhs = ((Account) other);
        return new EqualsBuilder().append(id, rhs.id).append(username, rhs.username).append(acct, rhs.acct).append(displayName, rhs.displayName).append(locked, rhs.locked).append(createdAt, rhs.createdAt).append(note, rhs.note).append(url, rhs.url).append(avatar, rhs.avatar).append(header, rhs.header).append(followersCount, rhs.followersCount).append(followingCount, rhs.followingCount).append(statusesCount, rhs.statusesCount).isEquals();
    }

}
