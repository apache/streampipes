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
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedSpringRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v2/element")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class PipelineElementImport extends AbstractAuthGuardedSpringRestResource {

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addElement(@RequestBody MultiValueMap<String, String> formDataMap) {
    if (formDataMap.containsKey("uri") && formDataMap.containsKey("publicElement")) {
      return ok(verifyAndAddElement(
          formDataMap.get("uri").get(0),
          getAuthenticatedUserSid(),
          Boolean.parseBoolean(formDataMap.get("publicElement").get(0))
      ));
    } else {
      return badRequest();
    }
  }

  @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Message> updateElement(@PathVariable("id") String elementId) {
    try {
      NamedStreamPipesEntity entity = find(elementId);
      String url = new ExtensionsServiceEndpointGenerator(entity).getEndpointResourceUrl();
      String payload = parseURIContent(url);
      return ok(Operations.verifyAndUpdateElement(payload));
    } catch (URISyntaxException | IOException | SepaParseException | NoServiceEndpointsAvailableException e) {
      e.printStackTrace();
      return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR, e.getMessage()));
    }
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Message> deleteElement(@PathVariable("id") String elementId) {
    IPipelineElementDescriptionStorage requestor = getPipelineElementStorage();
    var resourceManager = getSpResourceManager();
    String appId;
    try {
      if (requestor.existsDataProcessor(elementId)) {
        appId = requestor.getDataProcessorById(elementId).getAppId();
        resourceManager.manageDataProcessors().delete(elementId);
      } else if (requestor.existsDataStream(elementId)) {
        appId = requestor.getDataStreamById(elementId).getAppId();
        resourceManager.manageDataStreams().delete(elementId);
      } else if (requestor.existsDataSink(elementId)) {
        appId = requestor.getDataSinkById(elementId).getAppId();
        resourceManager.manageDataSinks().delete(elementId);
      } else if (requestor.existsAdapterDescription(elementId)) {
        appId = requestor.getAdapterById(elementId).getAppId();
        resourceManager.manageAdapterDescriptions().delete(elementId);
      } else {
        return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
            NotificationType.STORAGE_ERROR.description()));
      }
      AssetManager.deleteAsset(appId);
    } catch (IOException e) {
      return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
          NotificationType.STORAGE_ERROR.description()));
    }
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  private Message verifyAndAddElement(String uri,
                                      String principalSid,
                                      boolean publicElement) {
    return new EndpointItemParser().parseAndAddEndpointItem(uri, principalSid, publicElement);
  }

  private NamedStreamPipesEntity find(String elementId) {
    var extensionStorage = getPipelineElementStorage();
    if (extensionStorage.existsDataSink(elementId)) {
      return extensionStorage.getDataSinkById(elementId);
    } else if (extensionStorage.existsDataProcessor(elementId)) {
      return extensionStorage.getDataProcessorById(elementId);
    } else if (extensionStorage.existsDataStream(elementId)) {
      return extensionStorage.getDataStreamById(elementId);
    } else if (extensionStorage.existsAdapterDescription(elementId)) {
      return extensionStorage.getAdapterById(elementId);
    } else {
      throw new IllegalArgumentException("Could not find element for ID " + elementId);
    }
  }
}
