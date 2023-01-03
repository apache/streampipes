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

package org.apache.streampipes.connect.adapters.iss.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class IssModel {

  @SerializedName("iss_position")
  private IssPosition mIssPosition;
  @SerializedName("message")
  private String mMessage;
  @SerializedName("timestamp")
  private Long mTimestamp;

  public IssPosition getIssPosition() {
    return mIssPosition;
  }

  public void setIssPosition(IssPosition issPosition) {
    mIssPosition = issPosition;
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String message) {
    mMessage = message;
  }

  public Long getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(Long timestamp) {
    mTimestamp = timestamp;
  }

}
