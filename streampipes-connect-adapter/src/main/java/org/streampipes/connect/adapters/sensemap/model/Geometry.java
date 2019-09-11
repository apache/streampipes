
package org.streampipes.connect.adapters.sensemap.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Geometry {

    @SerializedName("coordinates")
    private List<Double> mCoordinates;
    @SerializedName("timestamp")
    private String mTimestamp;
    @SerializedName("type")
    private String mType;

    public List<Double> getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        mCoordinates = coordinates;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
