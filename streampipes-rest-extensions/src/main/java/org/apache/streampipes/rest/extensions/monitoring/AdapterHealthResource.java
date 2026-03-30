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

package org.apache.streampipes.rest.extensions.monitoring;

import org.apache.streampipes.extensions.management.monitoring.AdapterHealthCheckManager;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.rest.extensions.AbstractExtensionsResource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/adapter-health")
public class AdapterHealthResource extends AbstractExtensionsResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AdapterHealthStatus>> getAllAdapterHealth() {
    return ok(AdapterHealthCheckManager.INSTANCE.getAllHealthStatuses());
  }

  @GetMapping(value = "/{adapterId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdapterHealthStatus> getAdapterHealth(@PathVariable String adapterId) {
    return ok(AdapterHealthCheckManager.INSTANCE.getHealthStatus(adapterId));
  }

  @org.springframework.web.bind.annotation.PostMapping(value = "/{adapterId}/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> triggerAdapterHealthCheck(@PathVariable String adapterId) {
    AdapterHealthCheckManager.INSTANCE.triggerHealthCheck(adapterId);
    return ResponseEntity.ok().build();
  }
}
