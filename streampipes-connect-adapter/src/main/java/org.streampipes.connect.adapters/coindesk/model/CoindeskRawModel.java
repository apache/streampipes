
package org.streampipes.connect.adapter.specific.coindesk.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class CoindeskRawModel {

    @SerializedName("bpi")
    private Bpi mBpi;
    @SerializedName("chartName")
    private String mChartName;
    @SerializedName("disclaimer")
    private String mDisclaimer;
    @SerializedName("time")
    private Time mTime;

    public Bpi getBpi() {
        return mBpi;
    }

    public void setBpi(Bpi bpi) {
        mBpi = bpi;
    }

    public String getChartName() {
        return mChartName;
    }

    public void setChartName(String chartName) {
        mChartName = chartName;
    }

    public String getDisclaimer() {
        return mDisclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        mDisclaimer = disclaimer;
    }

    public Time getTime() {
        return mTime;
    }

    public void setTime(Time time) {
        mTime = time;
    }

}
