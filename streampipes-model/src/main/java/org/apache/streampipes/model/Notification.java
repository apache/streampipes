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

package org.apache.streampipes.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {

  private @JsonProperty("_id") @SerializedName("_id") String id;
  private @JsonProperty("_rev") @SerializedName("_rev") String rev;

  private String title;
  private Date createdAt;
  private long createdAtTimestamp;
  private String targetedAt;
  private String correspondingPipelineId;

  private String message;

  private Boolean read;

  public Notification(String title, Date createdAt, String targetedAt, String message) {
    this.title = title;
    this.createdAt = createdAt;
    this.targetedAt = targetedAt;
    this.message = message;
  }

  public Notification() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getTargetedAt() {
    return targetedAt;
  }

  public void setTargetedAt(String targetedAt) {
    this.targetedAt = targetedAt;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public Boolean isRead() {
    return read != null ? read : false;
  }

  public void setRead(Boolean read) {
    this.read = read;
  }

  public String getCorrespondingPipelineId() {
    return correspondingPipelineId;
  }

  public void setCorrespondingPipelineId(String correspondingPipelineId) {
    this.correspondingPipelineId = correspondingPipelineId;
  }

  public long getCreatedAtTimestamp() {
    return createdAtTimestamp;
  }

  public void setCreatedAtTimestamp(long createdAtTimestamp) {
    this.createdAtTimestamp = createdAtTimestamp;
  }
}
