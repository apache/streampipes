
package org.streampipes.connect.adapters.coindesk.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Bpi {

    @SerializedName("EUR")
    private EUR mEUR;
    @SerializedName("GBP")
    private GBP mGBP;
    @SerializedName("USD")
    private USD mUSD;

    public EUR getEUR() {
        return mEUR;
    }

    public void setEUR(EUR eUR) {
        mEUR = eUR;
    }

    public GBP getGBP() {
        return mGBP;
    }

    public void setGBP(GBP gBP) {
        mGBP = gBP;
    }

    public USD getUSD() {
        return mUSD;
    }

    public void setUSD(USD uSD) {
        mUSD = uSD;
    }

}
