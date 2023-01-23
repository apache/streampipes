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

package org.apache.streampipes.manager.execution.task;

import org.apache.streampipes.commons.MD5;
import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.manager.execution.PipelineExecutionInfo;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.pipeline.Pipeline;

public class UpdateGroupIdTask implements PipelineExecutionTask {
  @Override
  public void executeTask(Pipeline pipeline,
                          PipelineExecutionInfo executionInfo) {
    var sanitizedPipelineName = Utils.filterSpecialChar(pipeline.getName());
    pipeline.getSepas().forEach(processor -> updateGroupIds(processor, sanitizedPipelineName));
    pipeline.getActions().forEach(sink -> updateGroupIds(sink, sanitizedPipelineName));
  }

  private void updateGroupIds(InvocableStreamPipesEntity entity,
                              String sanitizedPipelineName) {
    entity.getInputStreams()
        .stream()
        .filter(is -> is.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol)
        .map(is -> is.getEventGrounding().getTransportProtocol())
        .map(KafkaTransportProtocol.class::cast)
        .forEach(tp -> tp.setGroupId(sanitizedPipelineName + MD5.crypt(tp.getElementId())));
  }
}
