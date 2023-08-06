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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.configuration.MessagingSettings;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.function.FunctionDefinition;
import org.apache.streampipes.model.message.SuccessMessage;

import java.util.List;

public class AdminApi extends AbstractClientApi implements IAdminApi {

  public AdminApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }

  @Override
  public void registerService(SpServiceRegistration serviceRegistration) {
    post(getExtensionsServiceRegistrationPath(), serviceRegistration);
  }

  @Override
  public void deregisterService(String serviceId) {
    post(getExtensionsServiceRegistrationPath().addToPath(serviceId));
  }

  @Override
  public void registerServiceConfiguration(SpServiceConfiguration serviceConfiguration) {
    post(getExtensionsServiceConfigurationPath(), serviceConfiguration);
  }

  @Override
  public SpServiceConfiguration getServiceConfiguration(String serviceGroup) {
    var opt = getSingleOpt(
        getExtensionsServiceConfigurationPath().addToPath(serviceGroup), SpServiceConfiguration.class);

    return opt.orElseGet(SpServiceConfiguration::new);
  }

  @Override
  public void registerAdapters(List<AdapterDescription> adapters) {
    post(getConnectPath(), adapters);
  }

  @Override
  public void registerFunctions(List<FunctionDefinition> functions) {
    post(getFunctionsPath(), functions);
  }

  @Override
  public void deregisterFunction(String functionId) {
    delete(getDeleteFunctionPath(functionId), SuccessMessage.class);
  }

  @Override
  public MessagingSettings getMessagingSettings() {
    return getSingle(getMessagingSettingsPath(), MessagingSettings.class);
  }

  private StreamPipesApiPath getExtensionsServiceRegistrationPath() {
    return StreamPipesApiPath
        .fromBaseApiPath()
        .addToPath("extensions-services");
  }

  private StreamPipesApiPath getExtensionsServiceConfigurationPath() {
    return StreamPipesApiPath
        .fromBaseApiPath()
        .addToPath("extensions-services-configurations");
  }

  private StreamPipesApiPath getMessagingSettingsPath() {
    return StreamPipesApiPath
        .fromBaseApiPath()
        .addToPath("messaging");
  }

  private StreamPipesApiPath getConnectPath() {
    return StreamPipesApiPath
        .fromBaseApiPath()
        .addToPath("connect")
        .addToPath("master")
        .addToPath("administration");
  }

  private StreamPipesApiPath getFunctionsPath() {
    return StreamPipesApiPath
        .fromBaseApiPath()
        .addToPath("functions");
  }

  private StreamPipesApiPath getDeleteFunctionPath(String functionId) {
    return getFunctionsPath().addToPath(functionId);
  }
}
