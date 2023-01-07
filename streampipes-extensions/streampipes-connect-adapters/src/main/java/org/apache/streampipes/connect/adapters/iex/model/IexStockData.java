/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.adapters.iex.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class IexStockData {

  @SerializedName("avgTotalVolume")
  private Long mAvgTotalVolume;
  @SerializedName("calculationPrice")
  private String mCalculationPrice;
  @SerializedName("change")
  private Double mChange;
  @SerializedName("changePercent")
  private Double mChangePercent;
  @SerializedName("close")
  private Double mClose;
  @SerializedName("closeTime")
  private Long mCloseTime;
  @SerializedName("companyName")
  private String mCompanyName;
  @SerializedName("delayedPrice")
  private Double mDelayedPrice;
  @SerializedName("delayedPriceTime")
  private Long mDelayedPriceTime;
  @SerializedName("extendedChange")
  private Double mExtendedChange;
  @SerializedName("extendedChangePercent")
  private Double mExtendedChangePercent;
  @SerializedName("extendedPrice")
  private Double mExtendedPrice;
  @SerializedName("extendedPriceTime")
  private Long mExtendedPriceTime;
  @SerializedName("high")
  private Double mHigh;
  @SerializedName("iexAskPrice")
  private Long mIexAskPrice;
  @SerializedName("iexAskSize")
  private Long mIexAskSize;
  @SerializedName("iexBidPrice")
  private Long mIexBidPrice;
  @SerializedName("iexBidSize")
  private Long mIexBidSize;
  @SerializedName("iexLastUpdated")
  private Long mIexLastUpdated;
  @SerializedName("iexMarketPercent")
  private Double mIexMarketPercent;
  @SerializedName("iexRealtimePrice")
  private Double mIexRealtimePrice;
  @SerializedName("iexRealtimeSize")
  private Long mIexRealtimeSize;
  @SerializedName("iexVolume")
  private Long mIexVolume;
  @SerializedName("latestPrice")
  private Double mLatestPrice;
  @SerializedName("latestSource")
  private String mLatestSource;
  @SerializedName("latestTime")
  private String mLatestTime;
  @SerializedName("latestUpdate")
  private Long mLatestUpdate;
  @SerializedName("latestVolume")
  private Long mLatestVolume;
  @SerializedName("low")
  private Double mLow;
  @SerializedName("marketCap")
  private Long mMarketCap;
  @SerializedName("open")
  private Double mOpen;
  @SerializedName("openTime")
  private Long mOpenTime;
  @SerializedName("peRatio")
  private Double mPeRatio;
  @SerializedName("previousClose")
  private Double mPreviousClose;
  @SerializedName("symbol")
  private String mSymbol;
  @SerializedName("week52High")
  private Double mWeek52High;
  @SerializedName("week52Low")
  private Long mWeek52Low;
  @SerializedName("ytdChange")
  private Double mYtdChange;

  public Long getAvgTotalVolume() {
    return mAvgTotalVolume;
  }

  public void setAvgTotalVolume(Long avgTotalVolume) {
    mAvgTotalVolume = avgTotalVolume;
  }

  public String getCalculationPrice() {
    return mCalculationPrice;
  }

  public void setCalculationPrice(String calculationPrice) {
    mCalculationPrice = calculationPrice;
  }

  public Double getChange() {
    return mChange;
  }

  public void setChange(Double change) {
    mChange = change;
  }

  public Double getChangePercent() {
    return mChangePercent;
  }

  public void setChangePercent(Double changePercent) {
    mChangePercent = changePercent;
  }

  public Double getClose() {
    return mClose;
  }

  public void setClose(Double close) {
    mClose = close;
  }

  public Long getCloseTime() {
    return mCloseTime;
  }

  public void setCloseTime(Long closeTime) {
    mCloseTime = closeTime;
  }

  public String getCompanyName() {
    return mCompanyName;
  }

  public void setCompanyName(String companyName) {
    mCompanyName = companyName;
  }

  public Double getDelayedPrice() {
    return mDelayedPrice;
  }

  public void setDelayedPrice(Double delayedPrice) {
    mDelayedPrice = delayedPrice;
  }

  public Long getDelayedPriceTime() {
    return mDelayedPriceTime;
  }

  public void setDelayedPriceTime(Long delayedPriceTime) {
    mDelayedPriceTime = delayedPriceTime;
  }

  public Double getExtendedChange() {
    return mExtendedChange;
  }

  public void setExtendedChange(Double extendedChange) {
    mExtendedChange = extendedChange;
  }

  public Double getExtendedChangePercent() {
    return mExtendedChangePercent;
  }

  public void setExtendedChangePercent(Double extendedChangePercent) {
    mExtendedChangePercent = extendedChangePercent;
  }

  public Double getExtendedPrice() {
    return mExtendedPrice;
  }

  public void setExtendedPrice(Double extendedPrice) {
    mExtendedPrice = extendedPrice;
  }

  public Long getExtendedPriceTime() {
    return mExtendedPriceTime;
  }

  public void setExtendedPriceTime(Long extendedPriceTime) {
    mExtendedPriceTime = extendedPriceTime;
  }

  public Double getHigh() {
    return mHigh;
  }

  public void setHigh(Double high) {
    mHigh = high;
  }

  public Long getIexAskPrice() {
    return mIexAskPrice;
  }

  public void setIexAskPrice(Long iexAskPrice) {
    mIexAskPrice = iexAskPrice;
  }

  public Long getIexAskSize() {
    return mIexAskSize;
  }

  public void setIexAskSize(Long iexAskSize) {
    mIexAskSize = iexAskSize;
  }

  public Long getIexBidPrice() {
    return mIexBidPrice;
  }

  public void setIexBidPrice(Long iexBidPrice) {
    mIexBidPrice = iexBidPrice;
  }

  public Long getIexBidSize() {
    return mIexBidSize;
  }

  public void setIexBidSize(Long iexBidSize) {
    mIexBidSize = iexBidSize;
  }

  public Long getIexLastUpdated() {
    return mIexLastUpdated;
  }

  public void setIexLastUpdated(Long iexLastUpdated) {
    mIexLastUpdated = iexLastUpdated;
  }

  public Double getIexMarketPercent() {
    return mIexMarketPercent;
  }

  public void setIexMarketPercent(Double iexMarketPercent) {
    mIexMarketPercent = iexMarketPercent;
  }

  public Double getIexRealtimePrice() {
    return mIexRealtimePrice;
  }

  public void setIexRealtimePrice(Double iexRealtimePrice) {
    mIexRealtimePrice = iexRealtimePrice;
  }

  public Long getIexRealtimeSize() {
    return mIexRealtimeSize;
  }

  public void setIexRealtimeSize(Long iexRealtimeSize) {
    mIexRealtimeSize = iexRealtimeSize;
  }

  public Long getIexVolume() {
    return mIexVolume;
  }

  public void setIexVolume(Long iexVolume) {
    mIexVolume = iexVolume;
  }

  public Double getLatestPrice() {
    return mLatestPrice;
  }

  public void setLatestPrice(Double latestPrice) {
    mLatestPrice = latestPrice;
  }

  public String getLatestSource() {
    return mLatestSource;
  }

  public void setLatestSource(String latestSource) {
    mLatestSource = latestSource;
  }

  public String getLatestTime() {
    return mLatestTime;
  }

  public void setLatestTime(String latestTime) {
    mLatestTime = latestTime;
  }

  public Long getLatestUpdate() {
    return mLatestUpdate;
  }

  public void setLatestUpdate(Long latestUpdate) {
    mLatestUpdate = latestUpdate;
  }

  public Long getLatestVolume() {
    return mLatestVolume;
  }

  public void setLatestVolume(Long latestVolume) {
    mLatestVolume = latestVolume;
  }

  public Double getLow() {
    return mLow;
  }

  public void setLow(Double low) {
    mLow = low;
  }

  public Long getMarketCap() {
    return mMarketCap;
  }

  public void setMarketCap(Long marketCap) {
    mMarketCap = marketCap;
  }

  public Double getOpen() {
    return mOpen;
  }

  public void setOpen(Double open) {
    mOpen = open;
  }

  public Long getOpenTime() {
    return mOpenTime;
  }

  public void setOpenTime(Long openTime) {
    mOpenTime = openTime;
  }

  public Double getPeRatio() {
    return mPeRatio;
  }

  public void setPeRatio(Double peRatio) {
    mPeRatio = peRatio;
  }

  public Double getPreviousClose() {
    return mPreviousClose;
  }

  public void setPreviousClose(Double previousClose) {
    mPreviousClose = previousClose;
  }

  public String getSymbol() {
    return mSymbol;
  }

  public void setSymbol(String symbol) {
    mSymbol = symbol;
  }

  public Double getWeek52High() {
    return mWeek52High;
  }

  public void setWeek52High(Double week52High) {
    mWeek52High = week52High;
  }

  public Long getWeek52Low() {
    return mWeek52Low;
  }

  public void setWeek52Low(Long week52Low) {
    mWeek52Low = week52Low;
  }

  public Double getYtdChange() {
    return mYtdChange;
  }

  public void setYtdChange(Double ytdChange) {
    mYtdChange = ytdChange;
  }

}
