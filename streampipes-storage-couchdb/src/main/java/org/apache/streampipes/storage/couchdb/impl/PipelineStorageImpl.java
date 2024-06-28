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

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineStorageImpl extends DefaultCrudStorage<Pipeline> implements IPipelineStorage {

  private static final String ADAPTER_VIEW = "adapters/used-adapters";
  private static final String ALL_PIPELINES_VIEW = "pipelines/all";

  public PipelineStorageImpl() {
    super(Utils::getCouchDbPipelineClient, Pipeline.class);
  }

  @Override
  public List<String> getPipelinesUsingAdapter(String adapterId) {
    List<JsonObject> pipelinesWithAdapter =
        couchDbClientSupplier
            .get()
            .view(ADAPTER_VIEW)
            .key(adapterId)
            .query(JsonObject.class);
    return pipelinesWithAdapter.stream().map(p -> p.get("value").getAsString()).collect(Collectors.toList());
  }

  @Override
  public List<Pipeline> findAll() {
    List<Pipeline> pipelines = findAll(ALL_PIPELINES_VIEW);

    List<Pipeline> result = new ArrayList<>();
    for (Pipeline p : pipelines) {
      if (p.getActions() != null) {
        result.add(p);
      }
    }
    return result;
  }
}
