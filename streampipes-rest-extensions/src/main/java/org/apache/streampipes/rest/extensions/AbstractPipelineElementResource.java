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

package org.apache.streampipes.rest.extensions;

import org.apache.streampipes.commons.media.ImageMimeTypeDetector;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.management.pe.AbstractPipelineElementManagement;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

public abstract class AbstractPipelineElementResource<
    T extends IStreamPipesPipelineElement<?>>
    extends AbstractExtensionsResource {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractPipelineElementResource.class);
  private final AbstractPipelineElementManagement<T> pipelineElementManagement;

  protected AbstractPipelineElementResource(AbstractPipelineElementManagement<T> pipelineElementManagement) {
    this.pipelineElementManagement = pipelineElementManagement;
  }

  @GetMapping(path = "{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public NamedStreamPipesEntity getDescription(@PathVariable("appId") String appId) {
    return pipelineElementManagement.getDescription(appId);
  }

  @GetMapping(path = "{appId}/assets", produces = "application/zip")
  public ResponseEntity<?> getAssets(@PathVariable("appId") String appId) {
    try {
      return ok(pipelineElementManagement.getAssets(appId));
    } catch (IOException e) {
      e.printStackTrace();
      return serverError();
    }
  }

  @GetMapping(path = "{appId}/assets/icon")
  public ResponseEntity<byte[]> getIconAsset(@PathVariable("appId") String appId) throws IOException {
    try {
      byte[] icon = pipelineElementManagement.getIconAsset(appId);
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(ImageMimeTypeDetector.detect(icon)))
          .body(icon);
    } catch (IOException e) {
      LOG.warn("No icon resource found for pipeline element {}", appId);
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).build();
    }
  }

  @GetMapping(path = "{appId}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> getDocumentationAsset(@PathVariable("appId") String appId) throws IOException {
    try {
      return ok(pipelineElementManagement.getDocumentationAsset(appId));
    } catch (IOException e) {
      LOG.warn("No documentation resource found for pipeline element {}", appId);
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).build();
    }
  }
}
