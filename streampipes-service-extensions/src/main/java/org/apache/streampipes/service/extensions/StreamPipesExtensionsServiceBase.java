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
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.rest.extensions.WelcomePage;
import org.apache.streampipes.rest.shared.exception.RestResponseLogMessageExceptionHandler;
import org.apache.streampipes.service.base.BaseNetworkingConfig;
import org.apache.streampipes.service.base.StreamPipesServiceBase;
import org.apache.streampipes.service.base.rest.ServiceHealthResource;
import org.apache.streampipes.service.extensions.function.StreamPipesFunctionHandler;
import org.apache.streampipes.service.extensions.security.WebSecurityConfig;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PreDestroy;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableAutoConfiguration
@Import({
    WebSecurityConfig.class,
    WelcomePage.class,
    ServiceHealthResource.class,
    RestResponseLogMessageExceptionHandler.class
})
@ComponentScan({"org.apache.streampipes.rest.extensions.*", "org.apache.streampipes.service.base.rest.*"})
public abstract class StreamPipesExtensionsServiceBase extends StreamPipesServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesExtensionsServiceBase.class);

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

  public void afterServiceRegistered(SpServiceDefinition serviceDef,
                                     SpServiceRegistration serviceReg) {
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();

    // register all migrations at StreamPipes Core
    var migrationConfigs = serviceDef.getMigrators().stream().map(IModelMigrator::config).toList();
    new CoreRequestSubmitter().submitMigrationRequest(client, migrationConfigs, serviceId(), serviceReg);

    // initialize all function instances
    StreamPipesFunctionHandler.INSTANCE.initializeFunctions(serviceDef.getServiceGroup());
  }

  public void startExtensionsService(Class<?> serviceClass,
                                     SpServiceDefinition serviceDef,
                                     BaseNetworkingConfig networkingConfig) throws UnknownHostException {
    var extensions = new ExtensionItemProvider().getAllItemDescriptions();
    var req = SpServiceRegistration.from(
        DefaultSpServiceTypes.EXT,
        serviceDef.getServiceGroup(),
        serviceId(),
        networkingConfig.getHost(),
        networkingConfig.getPort(),
        getServiceTags(extensions),
        getHealthCheckPath(),
        extensions);

    LOG.info("Registering service {} with id {} at core", req.getSvcGroup(), req.getSvcId());
    registerService(req);

    this.registerConfigs(serviceDef.getServiceGroup(), serviceDef.getServiceName(), serviceDef.getKvConfigs());
    this.startStreamPipesService(
        serviceClass,
        networkingConfig
    );

    this.afterServiceRegistered(serviceDef, req);
  }

  private void registerService(SpServiceRegistration serviceRegistration) {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    new CoreRequestSubmitter().submitRegistrationRequest(client, serviceRegistration);
  }

  protected Set<SpServiceTag> getServiceTags(Set<ExtensionItemDescription> extensions) {
    Set<SpServiceTag> tags = new HashSet<>();
    if (DeclarersSingleton.getInstance().getServiceDefinition() != null) {
      tags.add(SpServiceTag.create(SpServiceTagPrefix.SP_GROUP,
          DeclarersSingleton.getInstance().getServiceDefinition().getServiceGroup()));
    }
    tags.addAll(getExtensionsServiceTags(extensions));
    tags.addAll(new CustomServiceTagResolver(Environments.getEnvironment()).getCustomServiceTags());
    return tags;
  }

  protected void deregisterService(String serviceId) {
    LOG.info("Deregistering service (id={})...", serviceId);
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    client.adminApi().deregisterService(serviceId);
  }

  protected Set<SpServiceTag> getExtensionsServiceTags(Set<ExtensionItemDescription> extensions) {
    return extensions
        .stream()
        .map(e -> SpServiceTag.create(e.getServiceTagPrefix(), e.getAppId()))
        .collect(Collectors.toSet());
  }

  private void registerConfigs(String serviceGroup,
                               String serviceName,
                               List<ConfigItem> configs) {
    LOG.info("Registering {} service configs for service {}", configs.size(), serviceGroup);
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var serviceConfiguration = new SpServiceConfiguration(serviceGroup, serviceName, configs);
    client.adminApi().registerServiceConfiguration(serviceConfiguration);
  }

  @PreDestroy
  public void onExit() {
    new ExtensionsServiceShutdownHandler().onShutdown();
    StreamPipesFunctionHandler.INSTANCE.cleanupFunctions();
    deregisterService(DeclarersSingleton.getInstance().getServiceId());
  }

  public String serviceId() {
    return DeclarersSingleton.getInstance().getServiceId();
  }

  public abstract SpServiceDefinition provideServiceDefinition();

}
