
package org.streampipes.connect.rest.master.hackathon.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Description {

    @SerializedName("captions")
    private List<Caption> mCaptions;
    @SerializedName("tags")
    private List<String> mTags;

    public List<Caption> getCaptions() {
        return mCaptions;
    }

    public void setCaptions(List<Caption> captions) {
        mCaptions = captions;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        mTags = tags;
    }

}
