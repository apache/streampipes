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

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.model.labeling.Label;
import org.apache.streampipes.rest.core.base.impl.AbstractSpringRestResource;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/labeling/label")
public class LabelResource extends AbstractSpringRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Label>> getAllLabels() {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getAllLabels());
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> addLabel(@RequestBody Label label) {
    Category categoryForLabel = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getCategory(label.getCategoryId());
    if (categoryForLabel == null) {
      String resString = String.format("Category with categoryId %s does not exist", label.getCategoryId());
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    String labelId = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .storeLabel(label);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @GetMapping(path = "/{labelId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Label> getLabel(@PathVariable("labelId") String labelId) {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @PutMapping(path = "/{labelId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateLabel(@PathVariable("labelId") String labelId,
                                       @RequestBody Label label) {
    if (!labelId.equals(label.getId())) {
      String resString = "LabelId not the same as in message body";
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    Category categoryForLabel = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getCategory(label.getCategoryId());
    if (categoryForLabel == null) {
      String resString = String.format("Category with categoryId %s does not exist", label.getCategoryId());
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .updateLabel(label);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getLabel(labelId));
  }

  @DeleteMapping(path = "/{labelId}")
  public ResponseEntity<Void> deleteLabel(@PathVariable("labelId") String labelId) {
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .deleteLabel(labelId);
    return ok();
  }

  @GetMapping(path = "category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Label>> getLabelsForCategory(@PathVariable("categoryId") String categoryId) {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .getAllForCategory(categoryId));
  }
}
