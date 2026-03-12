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
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.schema.EventSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v2/connect/master/guess")
public class GuessResource extends AbstractAdapterResource<GuessManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(GuessResource.class);

  public GuessResource() {
    super(GuessManagement::new);
  }


  @PostMapping(
      path = "/sample",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> getSampleData(@RequestBody AdapterDescription adapterDescription)
      throws WorkerAdapterException {
    try {
      return ok(managementService.getSampleData(adapterDescription));
    } catch (NoServiceEndpointsAvailableException | IOException e) {
      LOG.error(e.getMessage());
      return serverError(SpLogMessage.from(e));
    }
  }

  @PostMapping(
      path = "/sample/transform",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<AdapterDescription> transformSample(@RequestBody AdapterDescription adapterDescription) throws
                                                                                                                AdapterException {

    var sampleData = managementService.transformSampleData(adapterDescription);

    return ok(sampleData);
  }

  @PostMapping(
      path = "/schema",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<EventSchema> guessSchema(@RequestBody AdapterDescription adapterDescription) {

    var eventSchema = managementService.guessSchema(adapterDescription);

    return ok(eventSchema);
  }

  @PostMapping(
      path = "/schema/preview",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> getAdapterEventPreview(@RequestBody AdapterDescription adapterDescription) {
    return ok(managementService.performAdapterEventPreview(adapterDescription));

  }
}
