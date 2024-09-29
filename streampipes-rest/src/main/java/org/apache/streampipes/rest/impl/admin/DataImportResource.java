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

import org.apache.streampipes.export.ImportManager;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/import")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class DataImportResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(DataImportResource.class);

  @PostMapping(path = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AssetExportConfiguration> getImportPreview(@RequestPart("file_upload") MultipartFile fileDetail)
          throws IOException {
    var importConfig = ImportManager.getImportPreview(fileDetail.getInputStream());
    return ok(importConfig);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> importData(@RequestPart("file_upload") MultipartFile fileDetail,
          @RequestPart("configuration") AssetExportConfiguration exportConfiguration) {
    try {
      ImportManager.performImport(fileDetail.getInputStream(), exportConfiguration, getAuthenticatedUserSid());
      return ok();
    } catch (IOException e) {
      LOG.error("An error occurred while importing resources", e);
      return fail();
    }

  }
}
