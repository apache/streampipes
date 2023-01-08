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

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class LastMeasurement {

  @SerializedName("createdAt")
  private String mCreatedAt;
  @SerializedName("value")
  private String mValue;

  public String getCreatedAt() {
    return mCreatedAt;
  }

  public void setCreatedAt(String createdAt) {
    mCreatedAt = createdAt;
  }

  public String getValue() {
    return mValue;
  }

  public void setValue(String value) {
    mValue = value;
  }

}
