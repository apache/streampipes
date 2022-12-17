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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.manager.matching.GroundingBuilder;
import org.apache.streampipes.manager.matching.v2.GroundingMatch;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplyGroundingStep extends AbstractPipelineValidationStep {

  private final Map<String, EventGrounding> sourceGroundingVisitorMap = new HashMap<>();

  @Override
  public void apply(NamedStreamPipesEntity source,
                    InvocableStreamPipesEntity target,
                    Set<InvocableStreamPipesEntity> allTargets,
                    List<PipelineElementValidationInfo> validationInfos) throws SpValidationException {

    List<MatchingResultMessage> errorLog = getNewErrorLog();
    boolean match = new GroundingMatch().match(
        getSourceGrounding(source),
        target.getSupportedGrounding(),
        errorLog
    );

    if (!match) {
      throw new SpValidationException(errorLog);
    } else {
      EventGrounding selectedGrounding;
      if (!sourceGroundingVisitorMap.containsKey(source.getDom())) {
        selectedGrounding = new GroundingBuilder(source, allTargets).getEventGrounding();
        sourceGroundingVisitorMap.put(source.getDom(), selectedGrounding);
      } else {
        selectedGrounding = new EventGrounding(sourceGroundingVisitorMap.get(source.getDom()));
      }

      if (source instanceof DataProcessorInvocation) {
        ((DataProcessorInvocation) source)
            .getOutputStream()
            .setEventGrounding(selectedGrounding);
      }

      target
          .getInputStreams()
          .get(getIndex(target))
          .setEventGrounding(selectedGrounding);

      if (target.getInputStreams().size() > 1) {
        this.visitorHistory.put(target.getDom(), 1);
      }
    }
  }

  private EventGrounding getSourceGrounding(NamedStreamPipesEntity source) {
    if (source instanceof SpDataStream) {
      return ((SpDataStream) source).getEventGrounding();
    } else {
      return ((DataProcessorInvocation) source).getOutputStream().getEventGrounding();
    }
  }
}
