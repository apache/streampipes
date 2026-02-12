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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.model.connect.ConnectTransformationScriptTemplate;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.api.ITransformationScriptTemplateStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/connect/master/script-templates")
public class TransformationScriptTemplateResource extends AbstractAuthGuardedRestResource {

  private final ITransformationScriptTemplateStorage templateStorage = StorageDispatcher
      .INSTANCE.getNoSqlStore().getTransformationScriptTemplateStorage();

  @GetMapping(
      value = "{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ConnectTransformationScriptTemplate findById(@PathVariable String id) {

    return templateStorage.getElementById(id);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ConnectTransformationScriptTemplate> getAll() {
    return templateStorage.findAll();
  }

  @PutMapping(
      value = "{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ConnectTransformationScriptTemplate update(@PathVariable String id,
                                                    @RequestBody ConnectTransformationScriptTemplate scriptTemplate) {
    if (!id.equals(scriptTemplate.getElementId())) {
      throw new IllegalArgumentException("ID in path and body do not match");
    }

    return templateStorage.updateElement(scriptTemplate);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public void create(@RequestBody ConnectTransformationScriptTemplate scriptTemplate) {
    templateStorage.persist(scriptTemplate);
  }

  @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public void delete(@PathVariable String id) {
    var scriptTemplate = templateStorage.getElementById(id);
    if (scriptTemplate == null) {
      throw new IllegalArgumentException("Certificate with ID " + id + " does not exist");
    }
    templateStorage.deleteElement(scriptTemplate);
  }
}
