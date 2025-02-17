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

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.NoSuchElementException;

public class DataProcessorStorageImpl extends DefaultCrudStorage<DataProcessorDescription>
    implements IDataProcessorStorage {


  public DataProcessorStorageImpl() {
    super(Utils::getCouchDbDataProcessorDescriptionClient, DataProcessorDescription.class);
  }

  @Override
  public DataProcessorDescription updateElement(DataProcessorDescription element) {
    element.setRev(getCurrentRev(element.getElementId()));
    update(element);
    return getElementById(element.getElementId());
  }

  @Override
  public DataProcessorDescription getFirstDataProcessorByAppId(String appId) {
    return getDataProcessorsByAppId(appId)
        .stream()
        .findFirst()
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public List<DataProcessorDescription> getDataProcessorsByAppId(String appId) {
    return this.findAll()
      .stream()
      .filter(p -> p.getAppId().equals(appId))
      .toList();
  }

  private String getCurrentRev(String elementId) {
    return find(elementId).get().getRev();
  }
}
