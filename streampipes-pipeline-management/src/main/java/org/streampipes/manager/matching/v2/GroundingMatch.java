/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.matching.v2;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.List;

public class GroundingMatch extends AbstractMatcher<EventGrounding, EventGrounding> {

  public GroundingMatch() {
    super(MatchingResultType.GROUNDING_MATCH);
  }

  @Override
  public boolean match(EventGrounding offer, EventGrounding requirement, List<MatchingResultMessage> errorLog) {
    return MatchingUtils.nullCheckRightNullDisallowed(offer, requirement) ||
            (matchProtocols(offer.getTransportProtocols(), requirement.getTransportProtocols(), errorLog) &&
                    matchFormats(offer.getTransportFormats(), requirement.getTransportFormats(), errorLog));
  }

  private boolean matchProtocols(List<TransportProtocol> offer, List<TransportProtocol> requirement, List<MatchingResultMessage> errorLog) {
    boolean match = MatchingUtils.nullCheckBothNullDisallowed(offer, requirement) &&
            requirement
                    .stream()
                    .anyMatch(req -> offer.stream().anyMatch(of -> new ProtocolMatch().match(of, req, errorLog)));

    if (!match) {
      buildErrorMessage(errorLog, MatchingResultType.PROTOCOL_MATCH, "Could not find matching protocol");
    }
    return match;
  }

  private boolean matchFormats(List<TransportFormat> offer, List<TransportFormat> requirement, List<MatchingResultMessage> errorLog) {
    boolean match = MatchingUtils.nullCheckBothNullDisallowed(offer, requirement) &&
            requirement
                    .stream()
                    .anyMatch(req -> offer.stream().anyMatch(of -> new FormatMatch().match(of, req, errorLog)));

    if (!match) {
      buildErrorMessage(errorLog, MatchingResultType.FORMAT_MATCH, "Could not find matching format");
    }
    return match;
  }

}
