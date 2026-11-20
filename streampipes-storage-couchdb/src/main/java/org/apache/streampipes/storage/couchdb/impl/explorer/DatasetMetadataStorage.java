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

package org.apache.streampipes.storage.couchdb.impl.explorer;

import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStore;
import org.apache.streampipes.storage.couchdb.impl.core.DefaultCrudStorage;

import java.util.List;

public class DatasetMetadataStorage extends DefaultCrudStorage<DatasetMetadata> implements IDatasetMetadataStorage {

  public static final String MEASUREMENT_BY_NAME_VIEW = "measurement/by-measure-name";

  public DatasetMetadataStorage(CouchDbStore store, Class<DatasetMetadata> clazz) {
    super(store, clazz);
  }

  @Override
  public DatasetMetadata getByMeasureName(String measureName) {
    return view(MEASUREMENT_BY_NAME_VIEW, clazz)
        .key(measureName)
        .first()
        .orElse(null);
  }

  @Override
  public List<DatasetMetadata> findAll() {
    return findAll(MEASUREMENT_BY_NAME_VIEW, clazz);
  }
}
