
package org.streampipes.connect.rest.master.hackathon.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Prediction {

    @SerializedName("probability")
    private Double mProbability;
    @SerializedName("tagId")
    private String mTagId;
    @SerializedName("tagName")
    private String mTagName;

    public Double getProbability() {
        return mProbability;
    }

    public void setProbability(Double probability) {
        mProbability = probability;
    }

    public String getTagId() {
        return mTagId;
    }

    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    public String getTagName() {
        return mTagName;
    }

    public void setTagName(String tagName) {
        mTagName = tagName;
    }

}
