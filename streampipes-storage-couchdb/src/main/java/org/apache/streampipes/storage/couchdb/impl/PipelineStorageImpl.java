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

import org.apache.streampipes.model.VirtualSensor;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineStorageImpl extends AbstractDao<Pipeline> implements IPipelineStorage {

  private static final String SYSTEM_USER = "system";
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
  public List<Pipeline> getAllPipelines() {
    List<Pipeline> pipelines = findAll(ALL_PIPELINES_VIEW);

    List<Pipeline> result = new ArrayList<>();
    for (Pipeline p : pipelines) {
      if (p.getActions() != null) {
        result.add(p);
      }
    }
    return result;
  }

  @Override
  public List<Pipeline> getSystemPipelines() {
    List<Pipeline> pipelines = getAllPipelines();
    return pipelines
        .stream()
        .filter(p -> p.getCreatedByUser().equals(SYSTEM_USER))
        .collect(Collectors.toList());
  }

  @Override
  public void storePipeline(Pipeline pipeline) {
    persist(pipeline);
  }

  @Override
  public void updatePipeline(Pipeline pipeline) {
    update(pipeline);
  }

  @Override
  public Pipeline getPipeline(String pipelineId) {
    return findWithNullIfEmpty(pipelineId);
  }

  @Override
  public void deletePipeline(String pipelineId) {
    delete(pipelineId);
  }

  @Override
  public void store(Pipeline object) {
    persist(object);
  }

  @Override
  public void storeVirtualSensor(String username, VirtualSensor virtualSensor) {
    CouchDbClient couchDbClient = couchDbClientSupplier.get();
    couchDbClient.save(virtualSensor);
    couchDbClient.shutdown();
  }

  @Override
  public List<VirtualSensor> getVirtualSensors(String username) {
    CouchDbClient couchDbClient = couchDbClientSupplier.get();
    List<VirtualSensor> virtualSensors = couchDbClient.view("_all_docs")
        .includeDocs(true)
        .query(VirtualSensor.class);
    couchDbClient.shutdown();
    return virtualSensors;
  }

}
