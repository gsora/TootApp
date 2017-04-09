
package MastodonTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class Status implements Serializable {

    private final static long serialVersionUID = 4983372382391510544L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("in_reply_to_id")
    @Expose
    private Object inReplyToId;
    @SerializedName("in_reply_to_account_id")
    @Expose
    private Object inReplyToAccountId;
    @SerializedName("sensitive")
    @Expose
    private Boolean sensitive;
    @SerializedName("spoiler_text")
    @Expose
    private String spoilerText;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("application")
    @Expose
    private Application application;
    @SerializedName("account")
    @Expose
    private Account account;
    @SerializedName("media_attachments")
    @Expose
    private List<MediaAttachment> mediaAttachments = null;
    @SerializedName("mentions")
    @Expose
    private List<Object> mentions = null;
    @SerializedName("tags")
    @Expose
    private List<Object> tags = null;
    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("reblogs_count")
    @Expose
    private Integer reblogsCount;
    @SerializedName("favourites_count")
    @Expose
    private Integer favouritesCount;
    @SerializedName("reblog")
    @Expose
    private Object reblog;
    @SerializedName("favourited")
    @Expose
    private Object favourited;
    @SerializedName("reblogged")
    @Expose
    private Object reblogged;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getInReplyToId() {
        return inReplyToId;
    }

    public void setInReplyToId(Object inReplyToId) {
        this.inReplyToId = inReplyToId;
    }

    public Object getInReplyToAccountId() {
        return inReplyToAccountId;
    }

    public void setInReplyToAccountId(Object inReplyToAccountId) {
        this.inReplyToAccountId = inReplyToAccountId;
    }

    public Boolean getSensitive() {
        return sensitive;
    }

    public void setSensitive(Boolean sensitive) {
        this.sensitive = sensitive;
    }

    public String getSpoilerText() {
        return spoilerText;
    }

    public void setSpoilerText(String spoilerText) {
        this.spoilerText = spoilerText;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<MediaAttachment> getMediaAttachments() {
        return mediaAttachments;
    }

    public void setMediaAttachments(List<MediaAttachment> mediaAttachments) {
        this.mediaAttachments = mediaAttachments;
    }

    public List<Object> getMentions() {
        return mentions;
    }

    public void setMentions(List<Object> mentions) {
        this.mentions = mentions;
    }

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getReblogsCount() {
        return reblogsCount;
    }

    public void setReblogsCount(Integer reblogsCount) {
        this.reblogsCount = reblogsCount;
    }

    public Integer getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(Integer favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Object getReblog() {
        return reblog;
    }

    public void setReblog(Object reblog) {
        this.reblog = reblog;
    }

    public Object getFavourited() {
        return favourited;
    }

    public void setFavourited(Object favourited) {
        this.favourited = favourited;
    }

    public Object getReblogged() {
        return reblogged;
    }

    public void setReblogged(Object reblogged) {
        this.reblogged = reblogged;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(createdAt).append(inReplyToId).append(inReplyToAccountId).append(sensitive).append(spoilerText).append(visibility).append(application).append(account).append(mediaAttachments).append(mentions).append(tags).append(uri).append(content).append(url).append(reblogsCount).append(favouritesCount).append(reblog).append(favourited).append(reblogged).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Status)) {
            return false;
        }
        Status rhs = ((Status) other);
        return new EqualsBuilder().append(id, rhs.id).append(createdAt, rhs.createdAt).append(inReplyToId, rhs.inReplyToId).append(inReplyToAccountId, rhs.inReplyToAccountId).append(sensitive, rhs.sensitive).append(spoilerText, rhs.spoilerText).append(visibility, rhs.visibility).append(application, rhs.application).append(account, rhs.account).append(mediaAttachments, rhs.mediaAttachments).append(mentions, rhs.mentions).append(tags, rhs.tags).append(uri, rhs.uri).append(content, rhs.content).append(url, rhs.url).append(reblogsCount, rhs.reblogsCount).append(favouritesCount, rhs.favouritesCount).append(reblog, rhs.reblog).append(favourited, rhs.favourited).append(reblogged, rhs.reblogged).isEquals();
    }

}
