
package org.streampipes.connect.adapters.coindesk.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class EUR {

    @SerializedName("code")
    private String mCode;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("rate")
    private String mRate;
    @SerializedName("rate_float")
    private Double mRateFloat;
    @SerializedName("symbol")
    private String mSymbol;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getRate() {
        return mRate;
    }

    public void setRate(String rate) {
        mRate = rate;
    }

    public Double getRateFloat() {
        return mRateFloat;
    }

    public void setRateFloat(Double rateFloat) {
        mRateFloat = rateFloat;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

}
