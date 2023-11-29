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

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractSpringRestResource;
import org.apache.streampipes.storage.api.IPipelineCategoryStorage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/pipelinecategories")
public class PipelineCategory extends AbstractSpringRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<org.apache.streampipes.model.pipeline.PipelineCategory>> getCategories() {
    return ok(getPipelineCategoryStorage()
        .getPipelineCategories());
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<? extends Message> addCategory(@RequestBody org.apache.streampipes.model.pipeline.PipelineCategory pipelineCategory) {
    boolean success = getPipelineCategoryStorage()
        .addPipelineCategory(pipelineCategory);
    if (success) {
      return ok(Notifications.success("Category successfully stored. "));
    } else {
      return ok(Notifications.error("Could not create category."));
    }
  }

  @DeleteMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<? extends Message> removeCategory(@PathVariable("categoryId") String categoryId) {
    boolean success = getPipelineCategoryStorage()
        .deletePipelineCategory(categoryId);
    if (success) {
      return ok(Notifications.success("Category successfully deleted. "));
    } else {
      return ok(Notifications.error("Could not delete category."));
    }
  }

  private IPipelineCategoryStorage getPipelineCategoryStorage() {
    return getNoSqlStorage().getPipelineCategoryStorageApi();
  }
}
