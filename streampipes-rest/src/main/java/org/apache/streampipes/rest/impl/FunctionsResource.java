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

import org.apache.streampipes.loadbalance.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.manager.function.FunctionRegistrationService;
import org.apache.streampipes.model.function.FunctionDefinition;
import org.apache.streampipes.model.function.FunctionState;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.function.IFunctionStateStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/functions")
public class FunctionsResource extends AbstractAuthGuardedRestResource {

  private final IFunctionStateStorage functionStateStorage;

  public FunctionsResource(IFunctionStateStorage functionStateStorage) {
    this.functionStateStorage = functionStateStorage;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Collection<FunctionDefinition>> getActiveFunctions() {
    return ok(FunctionRegistrationService.INSTANCE.getAllFunctions());
  }

  @GetMapping(path = "{functionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FunctionDefinition> getFunction(@PathVariable("functionId") String functionId) {
    return ok(FunctionRegistrationService.INSTANCE.getFunction(functionId));
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SuccessMessage> registerFunctions(@RequestBody List<FunctionDefinition> functions) {
    functions.forEach(FunctionRegistrationService.INSTANCE::registerFunction);
    return ok(Notifications.success("Function successfully registered"));
  }

  @DeleteMapping(path = "{functionId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SuccessMessage> deregisterFunction(@PathVariable("functionId") String functionId) {
    FunctionRegistrationService.INSTANCE.deregisterFunction(functionId);
    return ok(Notifications.success("Function successfully deregistered"));
  }

  @GetMapping(path = "{functionId}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SpMetricsEntry> getFunctionMetrics(@PathVariable("functionId") String functionId) {
    return ok(ExtensionsLogProvider.INSTANCE.getMetricInfosForResource(functionId));
  }

  @GetMapping(path = "{functionId}/logs")
  public ResponseEntity<List<SpLogEntry>> getFunctionLogs(@PathVariable("functionId") String functionId) {
    return ok(ExtensionsLogProvider.INSTANCE.getLogInfosForResource(functionId));
  }

  @GetMapping(path = "{functionId}/state", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> getFunctionState(@PathVariable("functionId") String functionId) {
    var functionState = functionStateStorage.getElementById(functionId);
    if (functionState != null) {
      return ok(functionState.getState());
    } else {
      throw new SpMessageException(HttpStatus.NOT_FOUND, Notifications.error("Function state not found"));
    }
  }

  @PutMapping(
      path = "{functionId}/state",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SuccessMessage> persistFunctionState(@PathVariable("functionId") String functionId,
                                                             @RequestBody Map<String, Object> state) {
    var existingFunctionState = functionStateStorage.getElementById(functionId);

    if (existingFunctionState != null) {
      existingFunctionState.setState(state);
      functionStateStorage.updateElement(existingFunctionState);
    } else {
      functionStateStorage.persist(new FunctionState(functionId, state));
    }

    return ok(Notifications.success("Function state successfully persisted"));
  }

  @DeleteMapping(path = "{functionId}/state", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SuccessMessage> deleteFunctionState(@PathVariable("functionId") String functionId) {
    var existingFunctionState = functionStateStorage.getElementById(functionId);

    if (existingFunctionState == null) {
      throw new SpMessageException(HttpStatus.NOT_FOUND, Notifications.error("Function state not found"));
    }

    functionStateStorage.deleteElementById(functionId);
    return ok(Notifications.success("Function state successfully deleted"));
  }
}
