
package org.streampipes.connect.rest.master.hackathon.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Tag {

    @SerializedName("confidence")
    private Double mConfidence;
    @SerializedName("name")
    private String mName;

    public Double getConfidence() {
        return mConfidence;
    }

    public void setConfidence(Double confidence) {
        mConfidence = confidence;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
