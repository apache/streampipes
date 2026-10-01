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

package org.apache.streampipes.dataexplorer.management;

import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.api.query.DatasetAdministrationBackend;

import java.util.List;
import java.util.Map;

public final class DatasetAdministrationService {
  private final IDatasetMetadataManagement catalog;
  private final DatasetAdministrationBackend backend;

  public DatasetAdministrationService(IDatasetMetadataManagement catalog, DatasetAdministrationBackend backend) {
    this.catalog = catalog;
    this.backend = backend;
  }

  public boolean deleteData(String name) {
    return catalog.getExistingMeasureByName(name).map(backend::delete).orElse(false);
  }

  public boolean deleteData(String name, Long startExclusive, Long endExclusive) {
    return catalog.getExistingMeasureByName(name)
        .map(dataset -> backend.deleteRange(dataset, startExclusive, endExclusive)).orElse(false);
  }

  public boolean deleteAllData() {
    return catalog.getAllMeasurements().stream().allMatch(backend::delete);
  }

  public Map<String, Object> dimensionValues(String name, List<String> fields) {
    var dataset = catalog.getExistingMeasureByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Unknown dataset: " + name));
    return backend.dimensionValues(dataset, fields);
  }
}
