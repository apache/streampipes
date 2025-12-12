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

package org.apache.streampipes.rest.extensions.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerSampleDataManagement;
import org.apache.streampipes.extensions.management.context.AdapterContextGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.shared.exception.SpLogMessageException;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker/guess")
public class AdapterWorkerSampleDataResource extends AbstractSharedRestInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerSampleDataResource.class);

  private final AdapterWorkerSampleDataManagement guessManagement;

  public AdapterWorkerSampleDataResource() {
    this.guessManagement = new AdapterWorkerSampleDataManagement(new AdapterContextGenerator().makeGuessSchemaContext());
  }

  public AdapterWorkerSampleDataResource(AdapterWorkerSampleDataManagement guessManagement) {
    this.guessManagement = guessManagement;
  }

  @PostMapping(
      path = "/sample",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SampleData> getSampleData(@RequestBody AdapterDescription adapterDescription)
      throws AdapterException {

    // TODO CHANGE: handle ParseExceptions or change to AdapterException
    try {
      var sampleData = guessManagement.getSampleData(adapterDescription);
      return ok(sampleData);
    } catch (ParseException e) {
      LOG.error("Error while parsing events: ", e);
      throw new SpLogMessageException(HttpStatus.INTERNAL_SERVER_ERROR, SpLogMessage.from(e));
    }

  }


}

