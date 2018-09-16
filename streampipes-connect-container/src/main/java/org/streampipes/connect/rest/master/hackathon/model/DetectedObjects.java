
package org.streampipes.connect.rest.master.hackathon.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class DetectedObjects {

    @SerializedName("categories")
    private List<Category> mCategories;
    @SerializedName("description")
    private Description mDescription;
    @SerializedName("metadata")
    private Metadata mMetadata;
    @SerializedName("requestId")
    private String mRequestId;
    @SerializedName("tags")
    private List<Tag> mTags;

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
    }

    public Description getDescription() {
        return mDescription;
    }

    public void setDescription(Description description) {
        mDescription = description;
    }

    public Metadata getMetadata() {
        return mMetadata;
    }

    public void setMetadata(Metadata metadata) {
        mMetadata = metadata;
    }

    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(String requestId) {
        mRequestId = requestId;
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }

}
