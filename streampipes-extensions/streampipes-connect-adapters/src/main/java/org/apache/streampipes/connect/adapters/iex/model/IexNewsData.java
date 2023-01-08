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
public class IexNewsData {

  @SerializedName("datetime")
  private Long mDatetime;
  @SerializedName("hasPaywall")
  private Boolean mHasPaywall;
  @SerializedName("headline")
  private String mHeadline;
  @SerializedName("image")
  private String mImage;
  @SerializedName("lang")
  private String mLang;
  @SerializedName("related")
  private String mRelated;
  @SerializedName("source")
  private String mSource;
  @SerializedName("summary")
  private String mSummary;
  @SerializedName("url")
  private String mUrl;

  public Long getDatetime() {
    return mDatetime;
  }

  public void setDatetime(Long datetime) {
    mDatetime = datetime;
  }

  public Boolean getHasPaywall() {
    return mHasPaywall;
  }

  public void setHasPaywall(Boolean hasPaywall) {
    mHasPaywall = hasPaywall;
  }

  public String getHeadline() {
    return mHeadline;
  }

  public void setHeadline(String headline) {
    mHeadline = headline;
  }

  public String getImage() {
    return mImage;
  }

  public void setImage(String image) {
    mImage = image;
  }

  public String getLang() {
    return mLang;
  }

  public void setLang(String lang) {
    mLang = lang;
  }

  public String getRelated() {
    return mRelated;
  }

  public void setRelated(String related) {
    mRelated = related;
  }

  public String getSource() {
    return mSource;
  }

  public void setSource(String source) {
    mSource = source;
  }

  public String getSummary() {
    return mSummary;
  }

  public void setSummary(String summary) {
    mSummary = summary;
  }

  public String getUrl() {
    return mUrl;
  }

  public void setUrl(String url) {
    mUrl = url;
  }

}
