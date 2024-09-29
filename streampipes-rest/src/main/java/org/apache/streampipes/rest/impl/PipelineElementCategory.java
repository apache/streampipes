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

import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.Category;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/categories")
public class PipelineElementCategory extends AbstractAuthGuardedRestResource {

  @GetMapping(path = "/ep", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Category>> getEps() {
    return ok(makeCategories(
            StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineElementDescriptionStorage().getAllDataStreams()));
  }

  @GetMapping(path = "/epa", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DataProcessorType[]> getEpaCategories() {
    return ok(DataProcessorType.values());
  }

  @GetMapping(path = "/adapter", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdapterType[]> getAdapterCategories() {
    return ok(AdapterType.values());
  }

  @GetMapping(path = "/ec", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DataSinkType[]> getEcCategories() {
    return ok(DataSinkType.values());
  }

  private List<Category> makeCategories(List<SpDataStream> streams) {
    return streams.stream().map(p -> new Category(p.getElementId(), p.getName(), p.getDescription()))
            .collect(Collectors.toList());
  }
}
