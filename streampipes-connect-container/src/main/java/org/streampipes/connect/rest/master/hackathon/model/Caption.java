
package org.streampipes.connect.rest.master.hackathon.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Caption {

    @SerializedName("confidence")
    private Double mConfidence;
    @SerializedName("text")
    private String mText;

    public Double getConfidence() {
        return mConfidence;
    }

    public void setConfidence(Double confidence) {
        mConfidence = confidence;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

}
