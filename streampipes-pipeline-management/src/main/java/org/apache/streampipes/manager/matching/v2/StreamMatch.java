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

import org.apache.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.client.matching.MatchingResultType;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;

public class StreamMatch extends AbstractMatcher<SpDataStream, SpDataStream> {

  public StreamMatch() {
    super(MatchingResultType.STREAM_MATCH);
  }

  @Override
  public boolean match(SpDataStream offer, SpDataStream requirement, List<MatchingResultMessage> errorLog) {
    return MatchingUtils.nullCheck(offer, requirement)
        || (checkSchemaMatch(offer.getEventSchema(), requirement.getEventSchema(), errorLog)
        && checkGroundingMatch(offer.getEventGrounding(), requirement.getEventGrounding(), errorLog));
  }

  public boolean matchIgnoreGrounding(SpDataStream offer, SpDataStream requirement,
                                      List<MatchingResultMessage> errorLog) {
    boolean match = /*MatchingUtils.nullCheckReqAllowed(offer, requirement) ||*/
        (checkSchemaMatch(offer.getEventSchema(), requirement.getEventSchema(), errorLog));
    return match;
  }

  private boolean checkGroundingMatch(EventGrounding offer,
                                      EventGrounding requirement, List<MatchingResultMessage> errorLog) {
    return new GroundingMatch().match(offer, requirement, errorLog);
  }

  private boolean checkSchemaMatch(EventSchema offer,
                                   EventSchema requirement, List<MatchingResultMessage> errorLog) {
    return new SchemaMatch().match(offer, requirement, errorLog);
  }

}
