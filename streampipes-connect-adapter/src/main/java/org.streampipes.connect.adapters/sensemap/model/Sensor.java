
package org.streampipes.connect.adapters.sensemap.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Sensor {

    @SerializedName("icon")
    private String mIcon;
    @SerializedName("lastMeasurement")
    private LastMeasurement mLastMeasurement;
    @SerializedName("sensorType")
    private String mSensorType;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("unit")
    private String mUnit;
    @SerializedName("_id")
    private String m_id;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public LastMeasurement getLastMeasurement() {
        return mLastMeasurement;
    }

    public void setLastMeasurement(LastMeasurement lastMeasurement) {
        mLastMeasurement = lastMeasurement;
    }

    public String getSensorType() {
        return mSensorType;
    }

    public void setSensorType(String sensorType) {
        mSensorType = sensorType;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public String get_id() {
        return m_id;
    }

    public void set_id(String _id) {
        m_id = _id;
    }

}
