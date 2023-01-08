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

package org.apache.streampipes.connect.adapters.flic;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class FlicOutput {

  @SerializedName("timestamp")
  private Long timestamp;

  @SerializedName("button_id")
  private String buttonID;

  @SerializedName("click_type")
  private String clickType;

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getButtonID() {
    return buttonID;
  }

  public void setButtonID(String buttonID) {
    this.buttonID = buttonID;
  }

  public String getClickType() {
    return clickType;
  }

  public void setClickType(float voltage) {
    this.clickType = clickType;
  }

}
