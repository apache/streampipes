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
import org.apache.streampipes.manager.pipeline.update.PipelineUpdateCoordinator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.resource.management.AdapterResourceManager;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import java.util.List;

public class AdapterUpdateManagement {

  private final AdapterMasterManagement adapterMasterManagement;
  private final AdapterResourceManager adapterResourceManager;
  private final DataStreamResourceManager dataStreamResourceManager;
  private final PipelineUpdateCoordinator pipelineUpdateCoordinator;

  public AdapterUpdateManagement(AdapterMasterManagement adapterMasterManagement,
                                 ExtensionServiceRequestManager requestManager) {
    this.adapterMasterManagement = adapterMasterManagement;
    this.adapterResourceManager = new SpResourceManager().manageAdapters();
    this.dataStreamResourceManager = new SpResourceManager().manageDataStreams();
    this.pipelineUpdateCoordinator = new PipelineUpdateCoordinator(requestManager);
  }

  public void updateAdapter(AdapterDescription ad)
      throws AdapterException {
    // update adapter in database 
    this.adapterResourceManager.encryptAndUpdate(ad);
    boolean shouldRestart = ad.isRunning();

    if (ad.isRunning()) {
      this.adapterMasterManagement.stopStreamAdapter(ad.getElementId(), true);
    }

    // update data source in database
    this.updateDataSource(ad);

    pipelineUpdateCoordinator.updatePipelines(ad);

    if (shouldRestart) {
      this.adapterMasterManagement.startStreamAdapter(ad.getElementId());
    }
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(AdapterDescription adapterDescription) {
    return pipelineUpdateCoordinator.checkPipelineMigrations(adapterDescription);
  }

  private void updateDataSource(AdapterDescription ad) {
    // get data source
    SpDataStream dataStream = this.dataStreamResourceManager.find(ad.getCorrespondingDataStreamElementId());

    SourcesManagement.updateDataStream(ad, dataStream);

    // Update data source in database
    this.dataStreamResourceManager.update(dataStream);
  }
}
