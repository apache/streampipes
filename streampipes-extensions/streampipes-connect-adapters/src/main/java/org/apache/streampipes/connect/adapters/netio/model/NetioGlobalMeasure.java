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

package org.apache.streampipes.connect.adapters.netio.model;


import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class NetioGlobalMeasure {

  @SerializedName("Voltage")
  private float voltage;

  @SerializedName("Frequency")
  private float frequency;

  @SerializedName("TotalCurrent")
  private float totalCurrent;

  @SerializedName("OverallPowerFactor")
  private float overallPowerFactor;

  @SerializedName("TotalLoad")
  private float totalLoad;

  @SerializedName("TotalEnergy")
  private float totalEnergy;

  public float getVoltage() {
    return voltage;
  }

  public void setVoltage(float voltage) {
    this.voltage = voltage;
  }

  public float getFrequency() {
    return frequency;
  }

  public void setFrequency(float frequency) {
    this.frequency = frequency;
  }

  public float getTotalCurrent() {
    return totalCurrent;
  }

  public void setTotalCurrent(float totalCurrent) {
    this.totalCurrent = totalCurrent;
  }

  public float getOverallPowerFactor() {
    return overallPowerFactor;
  }

  public void setOverallPowerFactor(float overallPowerFactor) {
    this.overallPowerFactor = overallPowerFactor;
  }

  public float getTotalLoad() {
    return totalLoad;
  }

  public void setTotalLoad(float totalLoad) {
    this.totalLoad = totalLoad;
  }

  public float getTotalEnergy() {
    return totalEnergy;
  }

  public void setTotalEnergy(float totalEnergy) {
    this.totalEnergy = totalEnergy;
  }
}
