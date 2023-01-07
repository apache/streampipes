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
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;
import java.util.List;

public class PrimitivePropertyMatch extends AbstractMatcher<EventPropertyPrimitive, EventPropertyPrimitive> {

  public PrimitivePropertyMatch() {
    super(MatchingResultType.PROPERTY_MATCH);
  }

  @Override
  public boolean match(EventPropertyPrimitive offer,
                       EventPropertyPrimitive requirement, List<MatchingResultMessage> errorLog) {

    boolean matchesUnit = unitMatch(offer.getMeasurementUnit(), requirement.getMeasurementUnit(), errorLog);
    boolean matchesDatatype = datatypeMatch(offer.getRuntimeType(), requirement.getRuntimeType(), errorLog);
    boolean matchesDomainProperty = domainPropertyMatch(offer.getDomainProperties(), requirement.getDomainProperties
        (), errorLog);

    return MatchingUtils.nullCheck(offer, requirement)
        || (matchesUnit && matchesDatatype && matchesDomainProperty);
  }

  private boolean domainPropertyMatch(List<URI> offer,
                                      List<URI> requirement, List<MatchingResultMessage> errorLog) {
    return new DomainPropertyMatch().match(offer, requirement, errorLog);
  }

  private boolean datatypeMatch(String offer, String requirement,
                                List<MatchingResultMessage> errorLog) {
    return new DatatypeMatch().match(offer, requirement, errorLog);
  }

  private boolean unitMatch(URI offer, URI requirement,
                            List<MatchingResultMessage> errorLog) {
    return new MeasurementUnitMatch().match(offer, requirement, errorLog);
  }

}
