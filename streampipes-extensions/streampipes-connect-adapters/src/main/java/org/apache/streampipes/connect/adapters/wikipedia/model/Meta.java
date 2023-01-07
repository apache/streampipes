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

package org.apache.streampipes.connect.adapters.wikipedia.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Meta {

  @SerializedName("domain")
  private String mDomain;
  @SerializedName("dt")
  private String mDt;
  @SerializedName("id")
  private String mId;
  @SerializedName("offset")
  private Long mOffset;
  @SerializedName("partition")
  private Long mPartition;
  @SerializedName("request_id")
  private String mRequestId;
  @SerializedName("schema_uri")
  private String mSchemaUri;
  @SerializedName("topic")
  private String mTopic;
  @SerializedName("uri")
  private String mUri;

  public String getDomain() {
    return mDomain;
  }

  public void setDomain(String domain) {
    mDomain = domain;
  }

  public String getDt() {
    return mDt;
  }

  public void setDt(String dt) {
    mDt = dt;
  }

  public String getId() {
    return mId;
  }

  public void setId(String id) {
    mId = id;
  }

  public Long getOffset() {
    return mOffset;
  }

  public void setOffset(Long offset) {
    mOffset = offset;
  }

  public Long getPartition() {
    return mPartition;
  }

  public void setPartition(Long partition) {
    mPartition = partition;
  }

  public String getRequestId() {
    return mRequestId;
  }

  public void setRequestId(String requestId) {
    mRequestId = requestId;
  }

  public String getSchemaUri() {
    return mSchemaUri;
  }

  public void setSchemaUri(String schemaUri) {
    mSchemaUri = schemaUri;
  }

  public String getTopic() {
    return mTopic;
  }

  public void setTopic(String topic) {
    mTopic = topic;
  }

  public String getUri() {
    return mUri;
  }

  public void setUri(String uri) {
    mUri = uri;
  }

}
