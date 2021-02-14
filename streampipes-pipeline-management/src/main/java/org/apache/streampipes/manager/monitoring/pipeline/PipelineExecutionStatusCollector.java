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

package org.apache.streampipes.manager.monitoring.pipeline;

import org.apache.streampipes.model.monitoring.PipelineElementMonitoringInfo;
import org.apache.streampipes.model.monitoring.PipelineMonitoringInfo;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;

public class PipelineExecutionStatusCollector {

  private String pipelineId;

  public PipelineExecutionStatusCollector(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public PipelineMonitoringInfo makePipelineMonitoringInfo() {
    Pipeline pipeline = getPipeline();

    PipelineMonitoringInfo monitoringInfo = new PipelineMonitoringInfo();
    monitoringInfo.setCreatedAt(pipeline.getCreatedAt());
    monitoringInfo.setStartedAt(pipeline.getStartedAt());
    monitoringInfo.setPipelineId(pipelineId);

    monitoringInfo.setPipelineElementMonitoringInfo(makePipelineElementMonitoringInfo(pipeline));

    return monitoringInfo;
  }

  private List<PipelineElementMonitoringInfo> makePipelineElementMonitoringInfo(Pipeline pipeline) {
    return new TopicInfoCollector(pipeline).makeMonitoringInfo();

  }

  private Pipeline getPipeline() {
    return StorageDispatcher
            .INSTANCE
            .getNoSqlStore()
            .getPipelineStorageAPI()
            .getPipeline(this.pipelineId);
  }
}
