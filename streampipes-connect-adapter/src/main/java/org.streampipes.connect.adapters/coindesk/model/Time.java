
package org.streampipes.connect.adapter.specific.coindesk.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Time {

    @SerializedName("updated")
    private String mUpdated;
    @SerializedName("updatedISO")
    private String mUpdatedISO;
    @SerializedName("updateduk")
    private String mUpdateduk;

    public String getUpdated() {
        return mUpdated;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public String getUpdatedISO() {
        return mUpdatedISO;
    }

    public void setUpdatedISO(String updatedISO) {
        mUpdatedISO = updatedISO;
    }

    public String getUpdateduk() {
        return mUpdateduk;
    }

    public void setUpdateduk(String updateduk) {
        mUpdateduk = updateduk;
    }

}
