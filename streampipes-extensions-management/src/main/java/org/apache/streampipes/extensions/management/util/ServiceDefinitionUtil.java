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
package org.apache.streampipes.extensions.management.util;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceDefinitionUtil {

  public static List<SpServiceTag> extractAppIds(Collection<IStreamPipesPipelineElement<?>> declarers) {
    return declarers
        .stream()
        .map(d -> {
          var model = d.declareConfig().getDescription();
          return SpServiceTag.create(getPrefix(model), model.getAppId());
        })
        .collect(Collectors.toList());
  }

  private static SpServiceTagPrefix getPrefix(NamedStreamPipesEntity entity) {
    if (entity instanceof SpDataStream) {
      return SpServiceTagPrefix.DATA_STREAM;
    } else if (entity instanceof DataProcessorDescription) {
      return SpServiceTagPrefix.DATA_PROCESSOR;
    } else if (entity instanceof DataSinkDescription) {
      return SpServiceTagPrefix.DATA_SINK;
    } else {
      throw new RuntimeException("Could not find service tag for entity " + entity.getClass().getSimpleName());
    }
  }

  public static List<SpServiceTag> extractAppIdsFromAdapters(Collection<StreamPipesAdapter> adapters) {
    return adapters
        .stream()
        .map(d -> SpServiceTag.create(
                SpServiceTagPrefix.ADAPTER,
                d.declareConfig().getAdapterDescription().getAppId()
            )
        )
        .collect(Collectors.toList());
  }

}
