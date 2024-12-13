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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.DescriptionManagement;
import org.apache.streampipes.manager.api.extensions.IExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/connect/master/description")
public class DescriptionResource extends AbstractAdapterResource<DescriptionManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(DescriptionResource.class);
  private final IExtensionsServiceEndpointGenerator endpointGenerator;

  public DescriptionResource() {
    super(DescriptionManagement::new);
    endpointGenerator = new ExtensionsServiceEndpointGenerator();
  }

  @GetMapping(path = "/adapters", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<List<AdapterDescription>> getAdapters() {
    List<AdapterDescription> result = managementService.getAdapters();

    return ok(result);
  }

  @GetMapping(path = "/{id}/assets", produces = "application/zip")
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getAdapterAssets(@PathVariable("id") String id) {
    try {
      String result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = getServiceResourceUrl(adapterDescription.getAppId());

        result = managementService.getAssets(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id, e);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @GetMapping(path = "/{id}/assets/icon", produces = "image/png")
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getAdapterIconAsset(@PathVariable("id") String id) {
    try {

      byte[] result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = getServiceResourceUrl(adapterDescription.getAppId());

        result = managementService.getIconAsset(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @GetMapping(path = "/{id}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getAdapterDocumentationAsset(@PathVariable("id") String id) {
    try {
      String result = null;

      Optional<AdapterDescription> adapterDescriptionOptional = managementService.getAdapter(id);
      if (adapterDescriptionOptional.isPresent()) {
        AdapterDescription adapterDescription = adapterDescriptionOptional.get();
        String workerUrl = getServiceResourceUrl(adapterDescription.getAppId());

        result = managementService.getDocumentationAsset(workerUrl);
      }

      if (result == null) {
        LOG.error("Not found adapter with id " + id);
        return fail();
      } else {
        return ok(result);
      }
    } catch (AdapterException e) {
      LOG.error("Not found adapter with id " + id, e);
      return fail();
    } catch (NoServiceEndpointsAvailableException e) {
      return fail();
    }
  }

  @DeleteMapping(path = "{adapterId}")
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> deleteAdapter(@PathVariable("adapterId") String adapterId) {
    try {
      this.managementService.deleteAdapterDescription(adapterId);
      return ok();
    } catch (SpRuntimeException e) {
      return badRequest(e);
    }
  }

  private String getServiceResourceUrl(String appId) throws NoServiceEndpointsAvailableException {
    return endpointGenerator.getEndpointResourceUrl(appId, SpServiceUrlProvider.ADAPTER);
  }
}
