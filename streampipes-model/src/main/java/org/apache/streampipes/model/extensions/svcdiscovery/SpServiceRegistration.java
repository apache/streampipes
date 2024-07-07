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

import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

@TsModel
public class SpServiceRegistration implements Storable {

  private String svcType;
  private String svcGroup;

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String svcId;
  private String scheme = "http";
  private String host;
  private int port;
  private Set<SpServiceTag> tags;
  private String healthCheckPath;
  private long firstTimeSeenUnhealthy = 0;
  private SpServiceStatus status = SpServiceStatus.REGISTERED;

  private Set<ExtensionItemDescription> providedExtensions;

  public SpServiceRegistration() {
  }

  public SpServiceRegistration(String svcType,
                               String svcGroup,
                               String svcId,
                               String host,
                               int port,
                               Set<SpServiceTag> tags,
                               String healthCheckPath,
                               Set<ExtensionItemDescription> providedExtensions) {
    this.svcType = svcType;
    this.svcGroup = svcGroup;
    this.svcId = svcId;
    this.host = host;
    this.port = port;
    this.tags = tags;
    this.healthCheckPath = healthCheckPath;
    this.providedExtensions = providedExtensions;
  }

  public static SpServiceRegistration from(String svcType,
                                           String svcGroup,
                                           String svcId,
                                           String host,
                                           Integer port,
                                           Set<SpServiceTag> tags,
                                           String healthCheckPath,
                                           Set<ExtensionItemDescription> providedExtensions) {
    return new SpServiceRegistration(svcType, svcGroup, svcId, host, port, tags, healthCheckPath, providedExtensions);
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

  public Set<SpServiceTag> getTags() {
    return tags;
  }

  public void setTags(Set<SpServiceTag> tags) {
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

  @Override
  public String getElementId() {
    return this.svcId;
  }

  @Override
  public void setElementId(String elementId) {
    this.svcId = elementId;
  }

  public String getScheme() {
    return scheme;
  }

  public String getServiceUrl() {
    return getScheme() + "://" + getHost() + ":" + getPort();
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

  public SpServiceStatus getStatus() {
    return status;
  }

  public void setStatus(SpServiceStatus status) {
    this.status = status;
  }

  public Set<ExtensionItemDescription> getProvidedExtensions() {
    return providedExtensions;
  }

  public void setProvidedExtensions(Set<ExtensionItemDescription> providedExtensions) {
    this.providedExtensions = providedExtensions;
  }
}
