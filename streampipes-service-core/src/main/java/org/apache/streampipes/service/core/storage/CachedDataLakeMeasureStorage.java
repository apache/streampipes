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

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;

public class CachedDataLakeMeasureStorage
    extends AbstractCachedCrudStorage<DataLakeMeasure, IDataLakeMeasureStorage>
    implements IDataLakeMeasureStorage {

  static final String CACHE_NAME = "dataLakeMeasures";

  private static final String MEASURE_NAME_KEY_PREFIX = "name:";

  public CachedDataLakeMeasureStorage(IDataLakeMeasureStorage delegate,
                                      CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedDataLakeMeasureStorage(IDataLakeMeasureStorage delegate,
                               CacheManager cacheManager,
                               ObjectMapper objectMapper) {
    super(
        delegate,
        cacheManager,
        CACHE_NAME,
        objectMapper.copy().addMixIn(DataLakeMeasure.class, DataLakeMeasureCacheMixin.class),
        DataLakeMeasure.class
    );
  }

  @Override
  public DataLakeMeasure getByMeasureName(String measureName) {
    return getOrLoad(
        key(MEASURE_NAME_KEY_PREFIX, measureName),
        type(DataLakeMeasure.class),
        () -> delegate.getByMeasureName(measureName)
    );
  }

  private abstract static class DataLakeMeasureCacheMixin {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    abstract String getTimestampField();
  }
}
