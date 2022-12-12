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

import org.apache.streampipes.model.canvas.PipelineCanvasMetadata;
import org.apache.streampipes.storage.api.IPipelineCanvasMetadataStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class PipelineCanvasMetadataStorageImpl extends AbstractDao<PipelineCanvasMetadata>
    implements IPipelineCanvasMetadataStorage {

  public PipelineCanvasMetadataStorageImpl() {
    super(Utils::getCouchDbPipelineCanvasMetadataClient, PipelineCanvasMetadata.class);
  }

  @Override
  public List<PipelineCanvasMetadata> getAll() {
    return findAll();
  }

  @Override
  public void createElement(PipelineCanvasMetadata element) {
    persist(element);
  }

  @Override
  public PipelineCanvasMetadata getElementById(String id) {
    return find(id).orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public PipelineCanvasMetadata updateElement(PipelineCanvasMetadata element) {
    update(element);
    return find(element.getId()).orElseThrow(IllegalAccessError::new);
  }

  @Override
  public void deleteElement(PipelineCanvasMetadata element) {
    delete(element.getId());
  }

  @Override
  public PipelineCanvasMetadata getPipelineCanvasMetadataForPipeline(String pipelineId) {
    // TODO add CouchDB view
    return findAll()
        .stream()
        .filter(p -> p.getPipelineId().equals(pipelineId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
