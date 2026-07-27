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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.update.DataStreamUpdateManagement;
import org.apache.streampipes.manager.pipeline.update.DataStreamUpdatedEvent;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class AdapterUpdateManagement {

  private final AdapterMasterManagement adapterMasterManagement;
  private final AdapterResourceManager adapterResourceManager;
  private final DataStreamResourceManager dataStreamResourceManager;
  private final DataStreamUpdateManagement dataStreamUpdateManagement;
  private final ApplicationEventPublisher eventPublisher;

  public AdapterUpdateManagement(AdapterMasterManagement adapterMasterManagement,
                                 ExtensionServiceRequestManager requestManager,
                                 SpResourceManager resourceManager,
                                 ApplicationEventPublisher eventPublisher) {
    this.adapterMasterManagement = adapterMasterManagement;
    this.adapterResourceManager = resourceManager.manageAdapters();
    this.dataStreamResourceManager = resourceManager.manageDataStreams();
    this.dataStreamUpdateManagement = new DataStreamUpdateManagement(
        requestManager,
        resourceManager
    );
    this.eventPublisher = eventPublisher;
  }

  public void updateAdapter(AdapterDescription ad)
      throws AdapterException {
    // update adapter in database
    AdapterTransformationConfigDefaults.applyTo(ad);
    this.adapterResourceManager.encryptAndUpdate(ad);
    boolean shouldRestart = ad.isRunning();

    if (ad.isRunning()) {
      this.adapterMasterManagement.stopAdapter(ad.getElementId(), true);
    }

    // update data source
    var updatedDataStream = this.updateDataSource(ad);
    dataStreamUpdateManagement.updateDataStream(updatedDataStream);
    publishEvent(new DataStreamUpdatedEvent(updatedDataStream));

    if (shouldRestart) {
      this.adapterMasterManagement.startAdapter(ad.getElementId());
    }
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(AdapterDescription adapterDescription) {
    return dataStreamUpdateManagement.checkPipelineMigrations(toDataStreamUpdate(adapterDescription));
  }

  private SpDataStream updateDataSource(AdapterDescription ad) {
    // get data source
    SpDataStream dataStream = this.dataStreamResourceManager.find(ad.getCorrespondingDataStreamElementId());

    return SourcesManagement.updateDataStream(ad, dataStream);
  }

  private SpDataStream toDataStreamUpdate(AdapterDescription adapterDescription) {
    // create a new data stream because adapterDescription.getStream() misses the real id and name
    var dataStream = new SpDataStream();
    dataStream.setElementId(adapterDescription.getCorrespondingDataStreamElementId());
    dataStream.setName(adapterDescription.getName());
    dataStream.setEventSchema(adapterDescription.getEventSchema());
    return dataStream;
  }

  private void publishEvent(Object event) {
    if (eventPublisher != null) {
      eventPublisher.publishEvent(event);
    }
  }
}
