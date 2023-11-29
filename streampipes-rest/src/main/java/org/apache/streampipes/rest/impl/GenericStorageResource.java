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

import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedSpringRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/storage-generic")
public class GenericStorageResource extends AbstractAuthGuardedSpringRestResource {

  public static final String APP_DOC_NAME = "appDocName";

  private static final Logger LOG = LoggerFactory.getLogger(GenericStorageResource.class);

  @GetMapping(path = "{appDocName}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_GENERIC_STORAGE_PRIVILEGE)
  public ResponseEntity<?> getAll(@PathVariable(APP_DOC_NAME) String appDocName) {
    try {
      List<Map<String, Object>> assets = getGenericStorage().findAll(appDocName);
      return ok(assets);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @PostMapping(
      path = "{appDocName}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public ResponseEntity<?> create(@PathVariable(APP_DOC_NAME) String appDocName,
                         @RequestBody String document) {
    try {
      Map<String, Object> obj = getGenericStorage().create(document);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @GetMapping(path = "{appDocName}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_GENERIC_STORAGE_PRIVILEGE)
  public ResponseEntity<?> getCategory(@PathVariable(APP_DOC_NAME) String appDocName,
                              @PathVariable("id") String documentId) {
    try {
      Map<String, Object> obj = getGenericStorage().findOne(documentId);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @PutMapping(
      path = "{appDocName}/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public ResponseEntity<?> update(@PathVariable(APP_DOC_NAME) String appDocName,
                         @PathVariable("id") String documentId,
                         @RequestBody String document) {
    try {
      Map<String, Object> obj = getGenericStorage().update(documentId, document);
      return ok(obj);
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  @DeleteMapping(path = "{appDocName}/{id}/{rev}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_GENERIC_STORAGE_PRIVILEGE)
  public ResponseEntity<Void> delete(@PathVariable(APP_DOC_NAME) String appDocName,
                                     @PathVariable("id") String documentId,
                                     @PathVariable("rev") String rev) {
    try {
      getGenericStorage().delete(documentId, rev);
      return ok();
    } catch (IOException e) {
      LOG.error("Could not connect to storage", e);
      return fail();
    }
  }

  private IGenericStorage getGenericStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();
  }

}
