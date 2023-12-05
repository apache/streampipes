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

import org.apache.streampipes.manager.endpoint.EndpointFetcher;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpointItem;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.storage.api.IExtensionsServiceEndpointStorage;

import org.apache.http.client.fluent.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/rdfendpoints")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ExtensionsServiceEndpointResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ExtensionsServiceEndpoint>> getAllEndpoints() {
    //TODO: return the endpoint of passing services
    return ok(getEndpoints());
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> addRdfEndpoint(@RequestBody ExtensionsServiceEndpoint extensionsServiceEndpoint) {
    getRdfEndpointStorage()
        .addExtensionsServiceEndpoint(extensionsServiceEndpoint);

    return ok();
  }


  @DeleteMapping(
      path = "/{rdfEndpointId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> removeRdfEndpoint(@PathVariable("rdfEndpointId") String rdfEndpointId) {
    getRdfEndpointStorage()
        .removeExtensionsServiceEndpoint(rdfEndpointId);

    return ok();
  }

  @GetMapping(path = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ExtensionsServiceEndpointItem>> getEndpointContents() {
    List<ExtensionsServiceEndpoint> endpoints = getEndpoints();
    String username = getAuthenticatedUsername();

    var installedExtensions = getAllInstalledExtensions();
    List<ExtensionsServiceEndpointItem> items = Operations.getEndpointUriContents(endpoints);
    items.forEach(item -> item.setInstalled(isInstalled(installedExtensions, item.getAppId())));

    // also add installed elements that are currently not running or available
    items.addAll(getAllAdapterEndpoints(items));
    items.addAll(getAllDataStreamEndpoints(username, items));
    items.addAll(getAllDataProcessorEndpoints(username, items));
    items.addAll(getAllDataSinkEndpoints(username, items));

    return ok(items);
  }

  @PostMapping(path = "/items/icon", produces = "image/png")
  public ResponseEntity<byte[]> getEndpointItemIcon(@RequestBody ExtensionsServiceEndpointItem endpointItem) {
    try {
      byte[] imageBytes = Request.Get(makeIconUrl(endpointItem)).execute().returnContent().asBytes();
      return ok(imageBytes);
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, e);
    }
  }

  private String makeIconUrl(ExtensionsServiceEndpointItem endpointItem) {
    return endpointItem.getUri() + "/assets/icon";
  }

  private List<ExtensionsServiceEndpoint> getEndpoints() {
    return new EndpointFetcher().getEndpoints();
  }

  private boolean isInstalled(List<NamedStreamPipesEntity> installedElements,
                              String appId) {
    return installedElements
        .stream()
        .anyMatch(e -> e.getAppId().equals(appId));
  }

  private List<NamedStreamPipesEntity> getAllInstalledExtensions() {
    List<NamedStreamPipesEntity> elements = new ArrayList<>();
    elements.addAll(getAllAdapters());
    elements.addAll(getAllDataStreams());
    elements.addAll(getAllDataProcessors());
    elements.addAll(getAllDataSinks());
    return elements;
  }

  private List<ExtensionsServiceEndpointItem> getAllAdapterEndpoints(
      List<ExtensionsServiceEndpointItem> existingItems) {
    return getAllAdapters()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.getAppId().equals(item.getAppId())))
        .map(adapter -> makeItem(adapter, "adapter"))
        .collect(Collectors.toList());
  }

  private List<ExtensionsServiceEndpointItem> getAllDataStreamEndpoints(
      String username,
      List<ExtensionsServiceEndpointItem> existingItems) {
    return getAllDataStreams()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.getAppId().equals(item.getAppId())))
        .filter(s -> !s.isInternallyManaged())
        .map(stream -> makeItem(stream, "stream"))
        .collect(Collectors.toList());
  }


  private List<ExtensionsServiceEndpointItem> getAllDataProcessorEndpoints(
      String username,
      List<ExtensionsServiceEndpointItem> existingItems) {

    return getAllDataProcessors()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.getAppId().equals(item.getAppId())))
        .map(source -> makeItem(source, "sepa"))
        .collect(Collectors.toList());
  }

  private List<ExtensionsServiceEndpointItem> getAllDataSinkEndpoints(
      String username,
      List<ExtensionsServiceEndpointItem> existingItems) {

    return getAllDataSinks()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.getAppId().equals(item.getAppId())))
        .map(source -> makeItem(source, "action"))
        .collect(Collectors.toList());
  }

  private ExtensionsServiceEndpointItem makeItem(NamedStreamPipesEntity entity, String type) {
    ExtensionsServiceEndpointItem endpoint = new ExtensionsServiceEndpointItem();
    endpoint.setInstalled(true);
    endpoint.setDescription(entity.getDescription());
    endpoint.setName(entity.getName());
    endpoint.setAppId(entity.getAppId());
    endpoint.setType(type);
    endpoint.setAvailable(false);
    endpoint.setElementId(entity.getElementId());
    endpoint.setUri(entity.getElementId());
    endpoint.setEditable(!(entity.isInternallyManaged()));
    endpoint.setIncludesIcon(entity.isIncludesAssets() && entity.getIncludedAssets().contains(Assets.ICON));
    endpoint.setIncludesDocs(entity.isIncludesAssets() && entity.getIncludedAssets().contains(Assets.DOCUMENTATION));
    return endpoint;
  }

  private List<AdapterDescription> getAllAdapters() {
    return getNoSqlStorage().getAdapterDescriptionStorage().getAllAdapters();
  }

  private List<SpDataStream> getAllDataStreams() {
    return getNoSqlStorage().getDataStreamStorage().getAll()
        .stream()
        .filter(stream -> !stream.isInternallyManaged())
        .toList();
  }

  private List<DataProcessorDescription> getAllDataProcessors() {
    return getNoSqlStorage().getDataProcessorStorage().getAll();
  }

  private List<DataSinkDescription> getAllDataSinks() {
    return getNoSqlStorage().getDataSinkStorage().getAll();
  }

  private IExtensionsServiceEndpointStorage getRdfEndpointStorage() {
    return getNoSqlStorage().getRdfEndpointStorage();
  }
}
