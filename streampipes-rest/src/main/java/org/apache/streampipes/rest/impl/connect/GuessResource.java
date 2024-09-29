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
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/connect/master/guess")
public class GuessResource extends AbstractAdapterResource<GuessManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(GuessResource.class);

  public GuessResource() {
    super(GuessManagement::new);
  }

  @PostMapping(path = "/schema", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> guessSchema(@RequestBody AdapterDescription adapterDescription) {

    try {
      GuessSchema result = managementService.guessSchema(adapterDescription);

      return ok(result);
    } catch (ParseException e) {
      LOG.error("Error while parsing events: ", e);
      return badRequest(SpLogMessage.from(e));
    } catch (WorkerAdapterException e) {
      return serverError(e.getExceptionMessage());
    } catch (NoServiceEndpointsAvailableException | IOException e) {
      LOG.error(e.getMessage());
      return serverError(SpLogMessage.from(e));
    }
  }

  @PostMapping(path = "/schema/preview", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getAdapterEventPreview(@RequestBody AdapterEventPreview previewRequest) {
    try {
      return ok(managementService.performAdapterEventPreview(previewRequest));
    } catch (JsonProcessingException e) {
      return badRequest();
    }
  }
}
