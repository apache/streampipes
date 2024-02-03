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

package org.apache.streampipes.rest.impl.pe;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/streams")
public class DataStreamResource extends AbstractAuthGuardedRestResource {

  @GetMapping(path = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.elementId, 'READ')")
  public List<SpDataStream> getAvailable() {
    return getDataStreamResourceManager().findAll();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.elementId, 'READ')")
  public List<SpDataStream> get() {
    return getDataStreamResourceManager().findAllAsInvocation();
  }

  @DeleteMapping(path = "/{elementId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_DELETE_PIPELINE_ELEMENT_PRIVILEGE)
  public ResponseEntity<Message> delete(@PathVariable("elementId") String elementId) {
    getDataStreamResourceManager().delete(elementId);
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  @GetMapping(path = "/{elementId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  public ResponseEntity<?> getElement(@PathVariable("elementId") String elementId) {
    try {
      return ok(getDataStreamResourceManager().findAsInvocation(elementId));
    } catch (IllegalArgumentException e) {
      return notFound(SpLogMessage.from(e));
    }
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_PIPELINE_ELEMENT_PRIVILEGE)
  public ResponseEntity<?> addDataStream(@RequestBody SpDataStream dataStream) {
    try {
      getDataStreamResourceManager().add(dataStream, getAuthenticatedUserSid());
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    }
  }

  private DataStreamResourceManager getDataStreamResourceManager() {
    return getSpResourceManager().manageDataStreams();
  }

}
