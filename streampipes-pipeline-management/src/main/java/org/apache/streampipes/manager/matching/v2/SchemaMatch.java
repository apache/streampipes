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
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.client.matching.MatchingResultType;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;

public class SchemaMatch extends AbstractMatcher<EventSchema, EventSchema> {

  public SchemaMatch() {
    super(MatchingResultType.SCHEMA_MATCH);
  }

  @Override
  public boolean match(EventSchema offer, EventSchema requirement, List<MatchingResultMessage> errorLog) {
    boolean matches = MatchingUtils.nullCheck(offer, requirement)
        || requirement.getEventProperties()
        .stream()
        .allMatch(req -> offer
            .getEventProperties()
            .stream()
            .anyMatch(of -> new PropertyMatch().match(of, req, errorLog)));
    //if (!matches) buildErrorMessage(errorLog, builtText(requirement));
    return matches;

  }

  private String builtText(EventSchema requirement) {
    if (requirement == null || requirement.getEventProperties() == null) {
      return "-";
    } else {
      return "Required event properties: " + requirement.getEventProperties().size();
    }
  }

}
