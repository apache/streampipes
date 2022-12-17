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

package org.apache.streampipes.manager.matching.v2;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.graph.DataProcessorInvocation;

import java.util.ArrayList;
import java.util.List;

public class ElementVerification {

  private List<MatchingResultMessage> errorLog;

  public ElementVerification() {
    this.errorLog = new ArrayList<>();
  }

  public boolean verify(DataProcessorInvocation offer, InvocableStreamPipesEntity requirement) {
    return new StreamMatch()
        .matchIgnoreGrounding(offer.getOutputStream(),
            requirement.getStreamRequirements().get(0), errorLog)
        && new GroundingMatch().match(offer.getSupportedGrounding(), requirement.getSupportedGrounding(), errorLog);
  }

  public boolean verify(SpDataStream offer, InvocableStreamPipesEntity requirement) {
    return new StreamMatch().matchIgnoreGrounding(offer, requirement.getStreamRequirements().get(0), errorLog)
        && new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
  }

  public List<MatchingResultMessage> getErrorLog() {
    return errorLog;
  }
}
