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

import org.apache.streampipes.manager.health.CoreServiceStatusManager;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/setup")
public class Setup extends AbstractRestResource {

  private final ISpCoreConfigurationStorage storage = StorageDispatcher.INSTANCE.getNoSqlStore()
          .getSpCoreConfigurationStorage();

  @GetMapping(path = "/configured", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Endpoint is used by UI to determine whether the core is running upon startup", tags = {
      "Configurated"})
  public ResponseEntity<String> isConfigured() {
    JsonObject obj = new JsonObject();
    var statusManager = new CoreServiceStatusManager(storage);
    if (statusManager.isCoreReady()) {
      obj.addProperty("configured", true);
    } else {
      obj.addProperty("configured", false);
      obj.addProperty("setupRunning", false);
    }
    return ok(obj.toString());
  }
}
