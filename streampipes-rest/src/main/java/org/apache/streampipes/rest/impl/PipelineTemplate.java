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

import org.apache.streampipes.manager.template.compact.CompactPipelineTemplateManagement;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.model.template.PipelineTemplateGenerationRequest;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.constants.SpMediaType;
import org.apache.streampipes.rest.shared.exception.BadRequestException;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/pipeline-templates")
public class PipelineTemplate extends AbstractAuthGuardedRestResource {

  private final CRUDStorage<CompactPipelineTemplate> storage;
  private final CompactPipelineTemplateManagement templateManagement;

  public PipelineTemplate() {
    storage = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineTemplateStorage();
    templateManagement = new CompactPipelineTemplateManagement(
        storage,
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineElementDescriptionStorage()
    );
  }

  @GetMapping(
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public List<CompactPipelineTemplate> findAll() {
    return storage.findAll()
        .stream()
        .sorted(Comparator.comparing(CompactPipelineTemplate::getName))
        .toList();
  }

  @GetMapping(
      path = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public ResponseEntity<?> findById(@PathVariable("id") String id) {
    return ok(storage.getElementById(id));
  }


  @PostMapping(
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML},
      consumes = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public void create(@RequestBody CompactPipelineTemplate entity) {
    storage.persist(entity);
  }

  @PutMapping(path = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML},
      consumes = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public void update(@PathVariable("id") String id, @RequestBody CompactPipelineTemplate entity) {
    storage.updateElement(entity);
  }

  @DeleteMapping(path = "/{id}")
  public void delete(@PathVariable("id") String id) {
    storage.deleteElementById(id);
  }


  @PostMapping(path = "/{id}/pipeline",
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML},
      consumes = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public ResponseEntity<?> makePipelineFromTemplate(@RequestBody PipelineTemplateGenerationRequest request) {
    try {
      return ok(templateManagement.makePipeline(request).pipeline());
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(
      path = "/{id}/streams",
      produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  public ResponseEntity<Map<String, List<List<String>>>> getAvailableStreamsForTemplate(
      @PathVariable("id") String pipelineTemplateId) {
    try {
      return ok(templateManagement.getStreamsForTemplate(pipelineTemplateId));
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
