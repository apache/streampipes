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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.audit.api.AuditStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/admin/audit")
public class AuditResource extends AbstractAuthGuardedRestResource {
  private final SpResourceManager resourceManager;

  public AuditResource(SpResourceManager resourceManager) {
    this.resourceManager = resourceManager;
  }

  @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Inspect audit recording status", tags = {"Audit"})
  public AuditStatus status() {
    return resourceManager.getAuditService().status();
  }
  @GetMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Fetch a bounded page of audit events", tags = {"Audit"})
  public AuditPage events(@RequestParam String from, @RequestParam String to,
                          @RequestParam(required = false) String eventType,
                          @RequestParam(required = false) String actor,
                          @RequestParam(required = false) String outcome,
                          @RequestParam(defaultValue = "50") int limit,
                          @RequestParam(required = false) String cursor) {
    AuditQuery query;
    try {
      query = new AuditQuery(Instant.parse(from), Instant.parse(to), eventType, actor, outcome, limit, cursor);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid audit filters");
    }
    try {
      return resourceManager.getAuditService().query(query);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid audit cursor");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audit storage unavailable");
    }
  }

  @GetMapping(path = "/events/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Fetch audit event details", tags = {"Audit"})
  public AuditEntryDetails details(@PathVariable UUID eventId, @RequestParam String locator) {
    try {
      return resourceManager.getAuditService().find(eventId, locator)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Audit event no longer available"));
    } catch (ResponseStatusException e) {
      throw e;
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid audit locator");
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audit storage unavailable");
    }
  }

  @GetMapping(path = "/event-types", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "List registered audit event types", tags = {"Audit"})
  public List<String> eventTypes() {
    return resourceManager.getAuditService().eventTypes();
  }
}
