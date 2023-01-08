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
public class NetioPowerOutput {

  @SerializedName("ID")
  private int id;

  @SerializedName("Name")
  private String name;

  @SerializedName("State")
  private int state;

  @SerializedName("Action")
  private int action;

  @SerializedName("Delay")
  private int delay;

  @SerializedName("Current")
  private float current;

  @SerializedName("PowerFactor")
  private float powerFactor;

  @SerializedName("Load")
  private float load;

  @SerializedName("Energy")
  private float energy;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public float getCurrent() {
    return current;
  }

  public void setCurrent(float current) {
    this.current = current;
  }

  public float getPowerFactor() {
    return powerFactor;
  }

  public void setPowerFactor(float powerFactor) {
    this.powerFactor = powerFactor;
  }

  public float getLoad() {
    return load;
  }

  public void setLoad(float load) {
    this.load = load;
  }

  public float getEnergy() {
    return energy;
  }

  public void setEnergy(float energy) {
    this.energy = energy;
  }
}
