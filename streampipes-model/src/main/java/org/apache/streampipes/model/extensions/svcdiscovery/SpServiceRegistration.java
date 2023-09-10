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
package org.apache.streampipes.model.extensions.svcdiscovery;

import org.apache.streampipes.model.shared.annotation.TsModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@TsModel
public class SpServiceRegistration {

  private String svcType;
  private String svcGroup;

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String svcId;
  private String scheme = "http";
  private String host;
  private int port;
  private List<SpServiceTag> tags;
  private String healthCheckPath;
  private boolean healthy = true;
  private long firstTimeSeenUnhealthy = 0;

  public SpServiceRegistration() {
  }

  public SpServiceRegistration(String svcType,
                               String svcGroup,
                               String svcId,
                               String host,
                               int port,
                               List<SpServiceTag> tags,
                               String healthCheckPath) {
    this.svcType = svcType;
    this.svcGroup = svcGroup;
    this.svcId = svcId;
    this.host = host;
    this.port = port;
    this.tags = tags;
    this.healthCheckPath = healthCheckPath;
  }

  public static SpServiceRegistration from(String svcType,
                                           String svcGroup,
                                           String svcId,
                                           String host,
                                           Integer port,
                                           List<SpServiceTag> tags) {
    return new SpServiceRegistration(svcType, svcGroup, svcId, host, port, tags, "");
  }

  public static SpServiceRegistration from(String svcType,
                                           String svcGroup,
                                           String svcId,
                                           String host,
                                           Integer port,
                                           List<SpServiceTag> tags,
                                           String healthCheckPath) {
    return new SpServiceRegistration(svcType, svcGroup, svcId, host, port, tags, healthCheckPath);
  }

  public String getSvcGroup() {
    return svcGroup;
  }

  public void setSvcGroup(String svcGroup) {
    this.svcGroup = svcGroup;
  }

  public String getSvcId() {
    return svcId;
  }

  public void setSvcId(String svcId) {
    this.svcId = svcId;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public List<SpServiceTag> getTags() {
    return tags;
  }

  public void setTags(List<SpServiceTag> tags) {
    this.tags = tags;
  }

  public String getHealthCheckPath() {
    return healthCheckPath;
  }

  public void setHealthCheckPath(String healthCheckPath) {
    this.healthCheckPath = healthCheckPath;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public boolean isHealthy() {
    return healthy;
  }

  public void setHealthy(boolean healthy) {
    this.healthy = healthy;
  }

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public long getFirstTimeSeenUnhealthy() {
    return firstTimeSeenUnhealthy;
  }

  public void setFirstTimeSeenUnhealthy(long firstTimeSeenUnhealthy) {
    this.firstTimeSeenUnhealthy = firstTimeSeenUnhealthy;
  }

  public String getSvcType() {
    return svcType;
  }

  public void setSvcType(String svcType) {
    this.svcType = svcType;
  }
}
