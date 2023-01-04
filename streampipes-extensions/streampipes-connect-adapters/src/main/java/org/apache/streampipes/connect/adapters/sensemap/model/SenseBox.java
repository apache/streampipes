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

package org.apache.streampipes.connect.adapters.sensemap.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class SenseBox {

  @SerializedName("createdAt")
  private String mCreatedAt;
  @SerializedName("currentLocation")
  private CurrentLocation mCurrentLocation;
  @SerializedName("exposure")
  private String mExposure;
  @SerializedName("loc")
  private List<Loc> mLoc;
  @SerializedName("model")
  private String mModel;
  @SerializedName("name")
  private String mName;
  @SerializedName("sensors")
  private List<Sensor> mSensors;
  @SerializedName("updatedAt")
  private String mUpdatedAt;
  @SerializedName("_id")
  private String mId;

  public String getCreatedAt() {
    return mCreatedAt;
  }

  public void setCreatedAt(String createdAt) {
    mCreatedAt = createdAt;
  }

  public CurrentLocation getCurrentLocation() {
    return mCurrentLocation;
  }

  public void setCurrentLocation(CurrentLocation currentLocation) {
    mCurrentLocation = currentLocation;
  }

  public String getExposure() {
    return mExposure;
  }

  public void setExposure(String exposure) {
    mExposure = exposure;
  }

  public List<Loc> getLoc() {
    return mLoc;
  }

  public void setLoc(List<Loc> loc) {
    mLoc = loc;
  }

  public String getModel() {
    return mModel;
  }

  public void setModel(String model) {
    mModel = model;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public List<Sensor> getSensors() {
    return mSensors;
  }

  public void setSensors(List<Sensor> sensors) {
    mSensors = sensors;
  }

  public String getUpdatedAt() {
    return mUpdatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    mUpdatedAt = updatedAt;
  }

  public String get_id() {
    return mId;
  }

  public void set_id(String id) {
    mId = id;
  }

}
