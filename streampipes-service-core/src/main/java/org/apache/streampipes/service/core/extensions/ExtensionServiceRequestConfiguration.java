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
package org.apache.streampipes.service.core.extensions;

import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.HttpExtensionServiceRequestManager;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.pipeline.IDataProcessorStorage;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.user.management.service.RefreshTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class ExtensionServiceRequestConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionServiceRequestConfiguration.class);

  @Bean(destroyMethod = "close")
  public CoreNatsRequestReplyClient coreNatsRequestReplyClient() {
    var env = Environments.getEnvironment();
    return new CoreNatsRequestReplyClient(
        env.getNatsHost().getValueOrDefault(),
        env.getNatsPort().getValueOrDefault(),
        env.getNatsToken().getValueOrDefault(),
        Duration.ofSeconds(10)
    );
  }

  @Bean
  public NatsExtensionServiceRequestManager natsExtensionServiceRequestManager(
      CoreNatsRequestReplyClient coreNatsRequestReplyClient
  ) {
    var env = Environments.getEnvironment();
    var topicPrefix = env.getExtensionRequestTopicPrefix()
        .getValueOrReturn(ExtensionServiceBrokerTopics.DEFAULT_REQUEST_TOPIC_PREFIX);

    return new NatsExtensionServiceRequestManager(
        coreNatsRequestReplyClient,
        topicPrefix
    );
  }

  @Bean
  @Primary
  public ExtensionServiceRequestManager extensionServiceRequestManager(
      NatsExtensionServiceRequestManager natsExtensionServiceRequestManager,
      IExtensionsServiceStorage extensionsServiceStorage
  ) {
    var env = Environments.getEnvironment();

    var transportMode = CoreExtensionTransportMode.from(
        env.getCoreExtensionTransportMode().getValueOrDefault()
    );

    LOG.info("Configuring core for transport mode: {}", transportMode);

    return new TransportAwareExtensionServiceRequestManager(
        new HttpExtensionServiceRequestManager(),
        natsExtensionServiceRequestManager,
        transportMode,
        extensionsServiceStorage
    );
  }

  @Bean
  public SpResourceManager spResourceManager(IPermissionStorage permissionStorage,
                                             IChartStorage chartStorage,
                                             IAdapterStorage adapterStorage,
                                             @Qualifier("adapterDescriptionStorage") IAdapterStorage adapterDescriptionStorage,
                                             IDashboardStorage dashboardStorage,
                                             IAssetStorage assetStorage,
                                             IPipelineStorage pipelineStorage,
                                             IDatasetMetadataStorage datasetStorage,
                                             ISpCoreConfigurationStorage coreConfigurationStorage,
                                             IFileMetadataStorage fileMetadataStorage,
                                             IRoleStorage roleStorage,
                                             IUserGroupStorage userGroupStorage,
                                             IPrivilegeStorage privilegeStorage,
                                             IUserStorage userStorage,
                                             IDataProcessorStorage dataProcessorStorage,
                                             IDataSinkStorage dataSinkStorage,
                                             IDataStreamStorage dataStreamStorage,
                                             IPipelineElementDescriptionStorage descriptionStorage,
                                             IGenericStorage genericStorage,
                                             IUserActivationTokenStorage userActivationTokenStorage,
                                             IPasswordRecoveryTokenStorage passwordRecoveryTokenStorage,
                                             ICertificateStorage certificateStorage,
                                             IExtensionsServiceStorage extensionsServiceStorage,
                                             ISpServiceDiscovery serviceDiscovery,
                                             AuditService auditService) {
    return new SpResourceManager(
        permissionStorage,
        chartStorage,
        adapterStorage,
        adapterDescriptionStorage,
        assetStorage,
        dashboardStorage,
        pipelineStorage,
        datasetStorage,
        coreConfigurationStorage,
        fileMetadataStorage,
        roleStorage,
        userGroupStorage,
        privilegeStorage,
        userStorage,
        dataProcessorStorage,
        dataSinkStorage,
        dataStreamStorage,
        descriptionStorage,
        genericStorage,
        userActivationTokenStorage,
        passwordRecoveryTokenStorage,
        certificateStorage,
        extensionsServiceStorage,
        serviceDiscovery,
        auditService
    );
  }

  @Bean
  public RefreshTokenService refreshTokenService(IRefreshTokenStorage refreshTokenStorage) {
    return new RefreshTokenService(refreshTokenStorage);
  }

  @Bean
  public WorkerRestClient workerRestClient(ExtensionServiceRequestManager extensionServiceRequestManager,
                                           SpResourceManager resourceManager) {
    return new WorkerRestClient(extensionServiceRequestManager, resourceManager);
  }
}
