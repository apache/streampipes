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

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class DataLakeStorageImpl extends AbstractDao<DataLakeMeasure> implements IDataLakeStorage {

  public DataLakeStorageImpl() {
    super(Utils::getCouchDbDataLakeClient, DataLakeMeasure.class);
  }

  @Override
  public boolean storeDataLakeMeasure(DataLakeMeasure measure) {
    return persist(measure).k;
  }

  @Override
  public List<DataLakeMeasure> getAllDataLakeMeasures() {
    return findAll();
  }

  @Override
  public DataLakeMeasure findOne(String id) {
    return find(id).orElse(null);
  }

  @Override
  public void updateDataLakeMeasure(DataLakeMeasure measure) {
    update(measure);
  }

  @Override
  public void deleteDataLakeMeasure(String id) {
    delete(id);
  }
}
