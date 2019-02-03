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

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.schema.EventPropertyList;

import java.util.List;

public class ListPropertyMatch implements Matcher<EventPropertyList, EventPropertyList> {

  @Override
  public boolean match(EventPropertyList offer, EventPropertyList requirement, List<MatchingResultMessage> errorLog) {
    if (requirement.getEventProperty() == null) {
      return true;
    } else {
      return domainPropertyMatch(offer, requirement, errorLog) && listItemMatch(offer, requirement,
              errorLog);
    }
  }

  private boolean domainPropertyMatch(EventPropertyList offer, EventPropertyList requirement,
                                      List<MatchingResultMessage> errorLog) {
    return new DomainPropertyMatch().match(offer.getDomainProperties(), requirement.getDomainProperties(),
            errorLog);
  }

  private boolean listItemMatch(EventPropertyList offer, EventPropertyList requirement,
                                List<MatchingResultMessage> errorLog) {
    return new PropertyMatch().match(offer.getEventProperty(), requirement.getEventProperty(),
            errorLog);
  }
}
