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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.service.base.BaseNetworkingConfig;
import org.apache.streampipes.service.base.StreamPipesServiceBase;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PreDestroy;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class StreamPipesExtensionsServiceBase extends StreamPipesServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesExtensionsServiceBase.class);
  private static final int RETRY_INTERVAL_SECONDS = 3;

  public void init() {
    SpServiceDefinition serviceDef = provideServiceDefinition();
    init(serviceDef);
  }

  public void init(SpServiceDefinition serviceDef) {
    try {
      BaseNetworkingConfig networkingConfig = BaseNetworkingConfig.defaultResolution(serviceDef.getDefaultPort());
      String serviceId = serviceDef.getServiceGroup() + "-" + AUTO_GENERATED_SERVICE_ID;
      serviceDef.setServiceId(serviceId);
      DeclarersSingleton.getInstance().populate(networkingConfig.getHost(), networkingConfig.getPort(), serviceDef);

      startExtensionsService(this.getClass(), serviceDef, networkingConfig);
    } catch (UnknownHostException e) {
      LOG.error(
          "Could not auto-resolve host address - "
              + "please manually provide the hostname using the SP_HOST environment variable");
    }
  }

  public abstract SpServiceDefinition provideServiceDefinition();

  public abstract void afterServiceRegistered(SpServiceDefinition serviceDef);

  public void startExtensionsService(Class<?> serviceClass,
                                     SpServiceDefinition serviceDef,
                                     BaseNetworkingConfig networkingConfig) throws UnknownHostException {
    var req = SpServiceRegistration.from(
        DefaultSpServiceTypes.EXT,
        serviceDef.getServiceGroup(),
        serviceId(),
        networkingConfig.getHost(),
        networkingConfig.getPort(),
        getServiceTags(),
        getHealthCheckPath());

    LOG.info("Registering service {} with id {} at core", req.getSvcGroup(), req.getSvcId());
    registerService(req);

    this.registerConfigs(serviceDef.getServiceGroup(), serviceDef.getServiceName(), serviceDef.getKvConfigs());
    this.startStreamPipesService(
        serviceClass,
        networkingConfig
    );

    this.afterServiceRegistered(serviceDef);
  }

  private void registerService(SpServiceRegistration serviceRegistration) {
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    try {
      client.adminApi().registerService(serviceRegistration);
      LOG.info("Successfully registered service at core.");
    } catch (SpRuntimeException e) {
      LOG.warn(
          "Could not register at core at url {}. Trying again in {} seconds",
          client.getConnectionConfig().getBaseUrl(),
          RETRY_INTERVAL_SECONDS
      );
      try {
        TimeUnit.SECONDS.sleep(RETRY_INTERVAL_SECONDS);
        registerService(serviceRegistration);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  protected List<SpServiceTag> getServiceTags() {
    List<SpServiceTag> tags = new ArrayList<>();
    if (DeclarersSingleton.getInstance().getServiceDefinition() != null) {
      tags.add(SpServiceTag.create(SpServiceTagPrefix.SP_GROUP,
          DeclarersSingleton.getInstance().getServiceDefinition().getServiceGroup()));
    }
    tags.addAll(getExtensionsServiceTags());
    return tags;
  }

  protected void deregisterService(String serviceId) {
    LOG.info("Deregistering service (id={})...", serviceId);
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    client.adminApi().deregisterService(serviceId);
  }

  private void registerConfigs(String serviceGroup,
                               String serviceName,
                               List<ConfigItem> configs) {
    LOG.info("Registering {} service configs for service {}", configs.size(), serviceGroup);
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var serviceConfiguration = new SpServiceConfiguration(serviceGroup, serviceName, configs);
    client.adminApi().registerServiceConfiguration(serviceConfiguration);
  }

  protected abstract List<SpServiceTag> getExtensionsServiceTags();

  @PreDestroy
  public abstract void onExit();

  public String serviceId() {
    return DeclarersSingleton.getInstance().getServiceId();
  }

}
