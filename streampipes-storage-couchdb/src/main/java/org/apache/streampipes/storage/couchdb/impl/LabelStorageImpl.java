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

import org.apache.streampipes.model.labeling.Label;
import org.apache.streampipes.storage.api.ILabelStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;


public class LabelStorageImpl extends AbstractDao<Label> implements ILabelStorage {

  public LabelStorageImpl() {
    super(Utils::getCouchDbLabelClient, Label.class);
  }

  @Override
  public List<Label> getAllLabels() {
    return findAll();
  }

  @Override
  public String storeLabel(Label label) {
    return persist(label).v;
  }

  @Override
  public Label getLabel(String labelId) {
    return find(labelId).orElse(new Label());
  }

  @Override
  public void deleteLabel(String labelId) {
    delete(labelId);
  }

  @Override
  public void updateLabel(Label label) {
    update(label);
  }

  @Override
  public List<Label> getAllForCategory(String categoryId) {
    return couchDbClientSupplier.get()
        .view("categoryId/categoryId")
        .key(categoryId)
        .includeDocs(true)
        .query(clazz);
  }

  @Override
  public void deleteAllForCategory(String categoryId) {
    List<Label> labelsForCategory = getAllForCategory(categoryId);
    for (Label label : labelsForCategory) {
      delete(label.getId());
    }
  }
}
