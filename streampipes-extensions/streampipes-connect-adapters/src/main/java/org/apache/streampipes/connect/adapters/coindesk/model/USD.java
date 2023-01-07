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

package org.apache.streampipes.connect.adapters.coindesk.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class USD {

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
