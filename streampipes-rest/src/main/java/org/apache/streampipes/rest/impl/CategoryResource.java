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
@RequestMapping("/api/v2/labeling/category")
public class CategoryResource extends AbstractSpringRestResource {


  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Category>> getAll() {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getAllCategories()
    );
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Category> add(@RequestBody Category category) {
    String categoryId = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .storeCategory(category);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI().getCategory(categoryId));
  }

  @GetMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Category> getCategory(@PathVariable("categoryId") String categoryId) {
    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .getCategory(categoryId));
  }

  @PutMapping(
      path = "/{categoryId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> update(@PathVariable("categoryId") String categoryId,
                                  @RequestBody Category category) {
    if (!categoryId.equals(category.getId())) {
      String resString = "CategoryId not the same as in message body";
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("message", resString);
      return badRequest(errorDetails);
    }
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .updateCategory(category);

    return ok(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI().getCategory(categoryId));
  }

  @DeleteMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> delete(@PathVariable("categoryId") String key) {
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getCategoryStorageAPI()
        .deleteCategory(key);
    StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getLabelStorageAPI()
        .deleteAllForCategory(key);
    return ok();
  }
}
