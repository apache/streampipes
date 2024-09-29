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
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.shared.exception.SpLogMessageException;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker")
public class AdapterWorkerResource extends AbstractSharedRestInterface {

  private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerResource.class);

  private AdapterWorkerManagement adapterManagement;

  public AdapterWorkerResource() {
    adapterManagement = new AdapterWorkerManagement(RunningAdapterInstances.INSTANCE, DeclarersSingleton.getInstance());
  }

  public AdapterWorkerResource(AdapterWorkerManagement adapterManagement) {
    this.adapterManagement = adapterManagement;
  }

  @GetMapping(path = "/running", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Collection<AdapterDescription>> getRunningAdapterInstances() {
    return ok(adapterManagement.getAllRunningAdapterInstances());
  }

  @PostMapping(path = "/stream/invoke", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SuccessMessage> invokeAdapter(@RequestBody AdapterDescription adapterStreamDescription) {

    try {
      adapterManagement.invokeAdapter(adapterStreamDescription);
      String responseMessage = "Stream adapter with id " + adapterStreamDescription.getElementId()
              + " successfully started";
      logger.info(responseMessage);
      return ok(Notifications.success(responseMessage));
    } catch (AdapterException e) {
      logger.error("Error while starting adapter with id " + adapterStreamDescription.getElementId(), e);
      throw new SpLogMessageException(HttpStatus.INTERNAL_SERVER_ERROR, SpLogMessage.from(e));
    }
  }

  @PostMapping(path = "/stream/stop", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SuccessMessage> stopAdapter(@RequestBody AdapterDescription adapterStreamDescription) {

    String responseMessage;
    try {
      if (adapterStreamDescription.isRunning()) {
        adapterManagement.stopAdapter(adapterStreamDescription);
        responseMessage = "Stream adapter with id " + adapterStreamDescription.getElementId() + " successfully stopped";
      } else {
        responseMessage = "Stream adapter with id " + adapterStreamDescription.getElementId()
                + " seems not to be running";
      }
      logger.info(responseMessage);
      return ok(Notifications.success(responseMessage));
    } catch (AdapterException e) {
      logger.error("Error while stopping adapter with id " + adapterStreamDescription.getElementId(), e);
      throw new SpLogMessageException(HttpStatus.INTERNAL_SERVER_ERROR, SpLogMessage.from(e));
    }
  }

}
