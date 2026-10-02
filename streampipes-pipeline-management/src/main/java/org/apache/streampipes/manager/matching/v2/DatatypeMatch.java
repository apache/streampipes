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

import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.client.matching.MatchingResultType;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.util.List;
import java.util.Set;

public class DatatypeMatch extends AbstractMatcher<String, String> {

  private static final Set<String> NUMERIC_TYPES = Set.of(
      XSD.INTEGER.toString(), XSD.LONG.toString(), XSD.DOUBLE.toString(), XSD.FLOAT.toString());

  public DatatypeMatch() {
    super(MatchingResultType.DATATYPE_MATCH);
  }

  @Override
  public boolean match(String offer, String requirement, List<MatchingResultMessage> errorLog) {

    boolean match = requirement == null
                    || requirement.equals(offer)
                    || (SO.NUMBER.equals(requirement) && offer != null && NUMERIC_TYPES.contains(offer));

    if (!match) {
      buildErrorMessage(errorLog, requirement);
    }
    return match;
  }
}
