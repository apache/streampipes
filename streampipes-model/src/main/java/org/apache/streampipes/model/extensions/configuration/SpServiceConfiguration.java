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

package org.apache.streampipes.model.extensions.configuration;

import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@TsModel
public class SpServiceConfiguration implements Storable {

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String serviceGroup;

  String serviceName;
  List<ConfigItem> configs;

  public SpServiceConfiguration() {
  }

  public SpServiceConfiguration(String serviceGroup,
                                String serviceName,
                                List<ConfigItem> configs) {
    this.serviceGroup = serviceGroup;
    this.serviceName = serviceName;
    this.configs = configs;
  }

  public String getServiceGroup() {
    return serviceGroup;
  }

  public void setServiceGroup(String serviceGroup) {
    this.serviceGroup = serviceGroup;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public List<ConfigItem> getConfigs() {
    return configs;
  }

  public void setConfigs(List<ConfigItem> configs) {
    this.configs = configs;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return this.serviceGroup;
  }

  @Override
  public void setElementId(String elementId) {
    this.serviceGroup = elementId;
  }
}
