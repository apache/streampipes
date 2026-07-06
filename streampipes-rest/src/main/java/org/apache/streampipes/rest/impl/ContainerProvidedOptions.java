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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.remote.ContainerProvidedOptionsHandler;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/pe/options")
public class ContainerProvidedOptions extends AbstractRestResource {

  private final ContainerProvidedOptionsHandler containerProvidedOptionsHandler;

  public ContainerProvidedOptions(ExtensionServiceRequestManager extensionServiceRequestManager,
                                  SpResourceManager resourceManager) {
    this.containerProvidedOptionsHandler = new ContainerProvidedOptionsHandler(
        extensionServiceRequestManager, resourceManager
    );
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> fetchRemoteOptions(@RequestBody RuntimeOptionsRequest request) {
    try {
      return ok(containerProvidedOptionsHandler.fetchRemoteOptions(request));
    } catch (SpConfigurationException e) {
      return badRequest(SpLogMessage.from(e));
    } catch (SpRuntimeException e) {
      return serverError(SpLogMessage.from(e));
    }
  }
}
