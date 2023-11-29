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
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.WorkerAdministrationManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.connect.management.management.WorkerUrlProvider;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/connect/master/resolvable")
public class RuntimeResolvableResource extends AbstractAdapterResource<WorkerAdministrationManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(RuntimeResolvableResource.class);

  private final WorkerUrlProvider workerUrlProvider;

  public RuntimeResolvableResource() {
    super(WorkerAdministrationManagement::new);
    this.workerUrlProvider = new WorkerUrlProvider();
  }

  @PostMapping(
      path = "{id}/configurations",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> fetchConfigurations(@PathVariable("id") String appId,
                                               @RequestBody RuntimeOptionsRequest runtimeOptionsRequest) {

    try {
      String workerEndpoint = workerUrlProvider.getWorkerBaseUrl(appId);
      RuntimeOptionsResponse result = WorkerRestClient.getConfiguration(workerEndpoint, appId, runtimeOptionsRequest);

      return ok(result);
    } catch (AdapterException e) {
      LOG.error("Adapter exception occurred", e);
      return serverError(SpLogMessage.from(e));
    } catch (NoServiceEndpointsAvailableException e) {
      LOG.error("Could not find service endpoint for {} while fetching configuration", appId);
      return serverError(SpLogMessage.from(e));
    } catch (SpConfigurationException e) {
      LOG.error("Tried to fetch a runtime configuration with insufficient settings");
      return badRequest(SpLogMessage.from(e));
    }
  }

}
