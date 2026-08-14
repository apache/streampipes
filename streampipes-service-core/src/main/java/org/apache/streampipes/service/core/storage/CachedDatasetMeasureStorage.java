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
package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IDatasetMeasureStorage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;

public class CachedDatasetMeasureStorage
    extends AbstractCachedCrudStorage<DatasetMeasure, IDatasetMeasureStorage>
    implements IDatasetMeasureStorage {

  static final String CACHE_NAME = "datasetMeasures";

  private static final String MEASURE_NAME_KEY_PREFIX = "name:";

  public CachedDatasetMeasureStorage(IDatasetMeasureStorage delegate,
                                      CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedDatasetMeasureStorage(IDatasetMeasureStorage delegate,
                               CacheManager cacheManager,
                               ObjectMapper objectMapper) {
    super(
        delegate,
        cacheManager,
        CACHE_NAME,
        objectMapper.copy().addMixIn(DatasetMeasure.class, DatasetMeasureCacheMixin.class),
        DatasetMeasure.class
    );
  }

  @Override
  public DatasetMeasure getByMeasureName(String measureName) {
    return getOrLoad(
        key(MEASURE_NAME_KEY_PREFIX, measureName),
        type(DatasetMeasure.class),
        () -> delegate.getByMeasureName(measureName)
    );
  }

  private abstract static class DatasetMeasureCacheMixin {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    abstract String getTimestampField();
  }
}
