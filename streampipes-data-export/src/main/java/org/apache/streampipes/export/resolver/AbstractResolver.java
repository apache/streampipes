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

package org.apache.streampipes.export.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.export.utils.SerializationUtils;
import org.apache.streampipes.model.assets.AssetLink;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractResolver<T> {


  public Set<ExportItem> resolve(Set<AssetLink> assetLinks) {
    return assetLinks
      .stream()
      .map(link -> findDocument(link.getResourceId()))
      .map(this::convert)
      .collect(Collectors.toSet());
  }

  public String getSerializedDocument(String resourceId) throws JsonProcessingException {
    return SerializationUtils.getSpObjectMapper().writeValueAsString(findDocument(resourceId));
  }

  protected INoSqlStorage getNoSqlStore() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
  }

  public abstract T findDocument(String resourceId);

  public abstract ExportItem convert(T document);
}
