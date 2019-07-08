
package org.streampipes.connect.adapter.specific.sensemap.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

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
