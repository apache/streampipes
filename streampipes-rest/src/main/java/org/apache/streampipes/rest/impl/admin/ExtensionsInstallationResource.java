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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.extensions.ExtensionItemInstaller;
import org.apache.streampipes.model.extensions.ExtensionItemInstallationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.resource.management.AdapterDescriptionResourceManager;
import org.apache.streampipes.resource.management.DataProcessorResourceManager;
import org.apache.streampipes.resource.management.DataSinkResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v2/extension-installation")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ExtensionsInstallationResource extends AbstractAuthGuardedRestResource {

  private final ExtensionServiceRequestManager extensionServiceRequestManager;
  private final DataSinkResourceManager dataSinkResourceManager;
  private final DataProcessorResourceManager dataProcessorResourceManager;
  private final AdapterDescriptionResourceManager adapterDescriptionResourceManager;
  private final DataStreamResourceManager dataStreamResourceManager;
  private final SpResourceManager resourceManager;
  private final AssetManager assetManager;

  public ExtensionsInstallationResource(ExtensionServiceRequestManager extensionServiceRequestManager,
                                        SpResourceManager resourceManager) {
    this.resourceManager = resourceManager;
    this.extensionServiceRequestManager = extensionServiceRequestManager;
    this.dataSinkResourceManager = resourceManager.manageDataSinks();
    this.dataProcessorResourceManager = resourceManager.manageDataProcessors();
    this.adapterDescriptionResourceManager = resourceManager.manageAdapterDescriptions();
    this.dataStreamResourceManager = resourceManager.manageDataStreams();
    this.assetManager = new AssetManager(resourceManager.getCoreConfigurationStorage());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Message> addElement(@RequestBody ExtensionItemInstallationRequest installationReq) {
    try {
      var service = findSupportedService(installationReq);
      return ok(new ExtensionItemInstaller(service, extensionServiceRequestManager, resourceManager)
          .installExtension(installationReq, getAuthenticatedUserSid()));
    } catch (IOException | SepaParseException | NoServiceEndpointsAvailableException e) {
      return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR, e.getMessage()));
    }
  }

  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Message> updateElement(@RequestBody ExtensionItemInstallationRequest installationReq) {
    try {
      var service = findSupportedService(installationReq);
      return ok(new ExtensionItemInstaller(service, extensionServiceRequestManager, resourceManager)
          .updateExtension(installationReq));
    } catch (IOException | SepaParseException | NoServiceEndpointsAvailableException e) {
      return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR, e.getMessage()));
    }
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Message> deleteElement(@PathVariable("id") String elementId) {
    IPipelineElementDescriptionStorage requestor = getPipelineElementStorage();
    String appId;
    try {
      if (requestor.existsDataProcessor(elementId)) {
        appId = requestor.getDataProcessorById(elementId).getAppId();
        dataProcessorResourceManager.delete(elementId);
      } else if (requestor.existsDataStream(elementId)) {
        appId = requestor.getDataStreamById(elementId).getAppId();
        dataStreamResourceManager.delete(elementId);
      } else if (requestor.existsDataSink(elementId)) {
        appId = requestor.getDataSinkById(elementId).getAppId();
        dataSinkResourceManager.delete(elementId);
      } else if (requestor.existsAdapterDescription(elementId)) {
        appId = requestor.getAdapterById(elementId).getAppId();
        adapterDescriptionResourceManager.delete(elementId);
      } else {
        return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
            NotificationType.STORAGE_ERROR.description()));
      }
      assetManager.deleteAsset(appId);
    } catch (IOException e) {
      return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
          NotificationType.STORAGE_ERROR.description()));
    }
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  private SpServiceRegistration findSupportedService(ExtensionItemInstallationRequest installationReq) throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointGenerator().selectService(
        installationReq.appId(),
        SpServiceUrlProvider.valueOf(installationReq.serviceTagPrefix().name()),
        Set.of()
    );
  }
}
