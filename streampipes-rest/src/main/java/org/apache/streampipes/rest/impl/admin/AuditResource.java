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
import org.apache.streampipes.audit.api.AuditExportColumn;
import org.apache.streampipes.audit.api.AuditExportConfiguration;
import org.apache.streampipes.audit.api.AuditExportFormat;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.audit.api.AuditStatus;
import org.apache.streampipes.model.dataset.RetentionTimeConfig;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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

  @GetMapping(path = "/retention", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Inspect audit retention configuration", tags = {"Audit"})
  public RetentionTimeConfig retention() {
    return resourceManager.getCoreConfigurationStorage().get().getAuditRetentionConfiguration();
  }

  @PutMapping(path = "/retention", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Configure audit retention", tags = {"Audit"})
  public void configureRetention(@RequestBody RetentionTimeConfig config) {
    var coreConfiguration = resourceManager.getCoreConfigurationStorage().get();
    coreConfiguration.setAuditRetentionConfiguration(config);
    resourceManager.getCoreConfigurationStorage().updateElement(coreConfiguration);
  }

  @DeleteMapping(path = "/retention")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Delete audit retention configuration", tags = {"Audit"})
  public void deleteRetention() {
    var coreConfiguration = resourceManager.getCoreConfigurationStorage().get();
    coreConfiguration.setAuditRetentionConfiguration(null);
    resourceManager.getCoreConfigurationStorage().updateElement(coreConfiguration);
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

  @GetMapping(path = "/events/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(summary = "Download filtered audit events", tags = {"Audit"})
  public ResponseEntity<StreamingResponseBody> download(@RequestParam String from, @RequestParam String to,
                                                         @RequestParam(required = false) String eventType,
                                                         @RequestParam(required = false) String actor,
                                                         @RequestParam(required = false) String outcome,
                                                         @RequestParam(defaultValue = "xlsx") String format,
                                                         @RequestParam(required = false) String delimiter,
                                                         @RequestParam List<String> columns,
                                                         @RequestParam List<String> headers) {
    final AuditQuery query;
    final AuditExportConfiguration configuration;
    try {
      query = new AuditQuery(Instant.parse(from), Instant.parse(to), eventType, actor, outcome, 200, null);
      var exportFormat = AuditExportFormat.fromString(format);
      configuration = new AuditExportConfiguration(
          exportFormat, columns.stream().map(AuditExportColumn::fromId).toList(), headers,
          delimiter(exportFormat, delimiter));
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid audit export request");
    }

    StreamingResponseBody body = outputStream -> resourceManager.getAuditService()
        .export(query, configuration, outputStream);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    responseHeaders.setContentDispositionFormData(
        "attachment", "audit-log." + configuration.format().name().toLowerCase());
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(body);
  }

  private char delimiter(AuditExportFormat format, String delimiter) {
    if (format != AuditExportFormat.CSV || delimiter == null || delimiter.equals("comma")) {
      return ',';
    }
    if (delimiter.equals("semicolon")) {
      return ';';
    }
    throw new IllegalArgumentException("Invalid audit export delimiter");
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
