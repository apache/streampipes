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

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;

import java.util.List;

public class CachedPipelineStorage
    extends AbstractCachedCrudStorage<Pipeline, IPipelineStorage>
    implements IPipelineStorage {

  static final String CACHE_NAME = "pipelines";

  private static final String ADAPTER_ID_KEY_PREFIX = "adapter:";

  public CachedPipelineStorage(IPipelineStorage delegate,
                               CacheManager cacheManager) {
    this(delegate, cacheManager, JacksonSerializer.getObjectMapper());
  }

  CachedPipelineStorage(IPipelineStorage delegate,
                        CacheManager cacheManager,
                        ObjectMapper objectMapper) {
    super(delegate, cacheManager, CACHE_NAME, objectMapper, Pipeline.class);
  }

  @Override
  public List<String> getPipelinesUsingAdapter(String adapterId) {
    return getOrLoad(
        key(ADAPTER_ID_KEY_PREFIX, adapterId),
        listType(String.class),
        () -> delegate.getPipelinesUsingAdapter(adapterId)
    );
  }
}
