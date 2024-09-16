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

package org.apache.streampipes.manager.template;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Optional;

public class PipelineTemplateManagement {

  public PipelineTemplateInvocation prepareInvocation(String streamId,
                                                      String pipelineTemplateId) {
    SpDataStream dataStream = getDataStream(streamId);

    var pipelineTemplateDescriptionOpt = getPipelineTemplateDescription(pipelineTemplateId);
    if (pipelineTemplateDescriptionOpt.isPresent()) {
      PipelineTemplateInvocation invocation =
          new PipelineTemplateInvocationGenerator(
              dataStream,
              pipelineTemplateDescriptionOpt.get()
          ).generateInvocation();
      PipelineTemplateInvocation clonedInvocation = new PipelineTemplateInvocation(invocation);
      return new PipelineTemplateInvocation(clonedInvocation);
    } else {
      throw new IllegalArgumentException(String.format(
          "Could not find pipeline template with ID %s",
          pipelineTemplateId)
      );
    }
  }

  public PipelineOperationStatus createAndStartPipeline(PipelineTemplateInvocation pipelineTemplateInvocation,
                                                        String authenticatedUserSid) {
    return new PipelineTemplateInvocationHandler(
        authenticatedUserSid,
        pipelineTemplateInvocation
    ).handlePipelineInvocation();
  }

  private Optional<PipelineTemplateDescription> getPipelineTemplateDescription(String pipelineTemplateId) {
    return new PipelineTemplateGenerator()
        .getAllPipelineTemplates()
        .stream()
        .filter(pt -> pt.getAppId().equals(pipelineTemplateId))
        .findFirst();
  }

  private SpDataStream getDataStream(String streamId) {
    return getAllDataStreams()
        .stream()
        .filter(sp -> sp.getElementId().equals(streamId))
        .findFirst()
        .get();
  }

  private List<SpDataStream> getAllDataStreams() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineElementDescriptionStorage().getAllDataStreams();
  }
}
