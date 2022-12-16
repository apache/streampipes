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

import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class DataSinkStorageImpl extends AbstractDao<DataSinkDescription> implements IDataSinkStorage {


  public DataSinkStorageImpl() {
    super(Utils::getCouchDbDataSinkDescriptionClient, DataSinkDescription.class);
  }

  @Override
  public List<DataSinkDescription> getAll() {
    return findAll();
  }

  @Override
  public void createElement(DataSinkDescription element) {
    persist(element);
  }

  @Override
  public DataSinkDescription getElementById(String s) {
    return findWithNullIfEmpty(s);
  }

  @Override
  public DataSinkDescription updateElement(DataSinkDescription element) {
    element.setRev(getCurrentRev(element.getElementId()));
    update(element);
    return getElementById(element.getElementId());
  }

  @Override
  public void deleteElement(DataSinkDescription element) {
    delete(element.getElementId());
  }

  @Override
  public DataSinkDescription getDataSinkByAppId(String appId) {
    return getAll()
        .stream()
        .filter(s -> s.getAppId().equals(appId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  private String getCurrentRev(String elementId) {
    return find(elementId).get().getRev();
  }
}
