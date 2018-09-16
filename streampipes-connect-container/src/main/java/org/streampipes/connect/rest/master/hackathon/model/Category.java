
package org.streampipes.connect.rest.master.hackathon.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Category {

    @SerializedName("name")
    private String mName;
    @SerializedName("score")
    private Double mScore;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getScore() {
        return mScore;
    }

    public void setScore(Double score) {
        mScore = score;
    }

}
