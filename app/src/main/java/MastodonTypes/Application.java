
package MastodonTypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Application extends RealmObject implements Serializable {

    private final static long serialVersionUID = -6057618037440799795L;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("website")
    @Expose
    private String website;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(website).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Application)) {
            return false;
        }
        Application rhs = ((Application) other);
        return new EqualsBuilder().append(name, rhs.name).append(website, rhs.website).isEquals();
    }

}
