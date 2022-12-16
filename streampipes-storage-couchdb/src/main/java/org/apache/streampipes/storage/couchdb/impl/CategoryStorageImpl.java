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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.storage.api.ICategoryStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class CategoryStorageImpl extends AbstractDao<Category> implements ICategoryStorage {
  public CategoryStorageImpl() {
    super(Utils::getCouchDbCategoryClient, Category.class);
  }

  @Override
  public List<Category> getAllCategories() {
    return findAll();
  }

  @Override
  public String storeCategory(Category category) {
    return persist(category).v;
  }

  @Override
  public void updateCategory(Category category) {
    update(category);
  }

  @Override
  public void deleteCategory(String key) {
    delete(key);
  }

  @Override
  public Category getCategory(String key) {
    return find(key).orElse(null);
  }
}
