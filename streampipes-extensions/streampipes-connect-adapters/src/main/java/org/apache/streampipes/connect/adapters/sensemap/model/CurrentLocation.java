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
public class CurrentLocation {

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
