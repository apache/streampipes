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

import org.apache.streampipes.model.client.matching.MatchingResultFactory;
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.client.matching.MatchingResultType;

import java.util.List;

public abstract class AbstractMatcher<T, V> implements Matcher<T, V> {

  protected MatchingResultType matchingResultType;

  public AbstractMatcher(MatchingResultType matchingResultType) {
    this.matchingResultType = matchingResultType;
  }

  protected void buildErrorMessage(List<MatchingResultMessage> errorLog, String rightSubject) {
    MatchingResultMessage message = MatchingResultFactory.build(matchingResultType, false, rightSubject);
    if (!containsMessage(errorLog, message)) {
      errorLog.add(message);
    }
  }

  protected void buildErrorMessage(List<MatchingResultMessage> errorLog, MatchingResultType type, String rightSubject) {
    errorLog.add(MatchingResultFactory.build(type, false, rightSubject));
  }

  public abstract boolean match(T offer, V requirement, List<MatchingResultMessage> errorLog);

  private boolean containsMessage(List<MatchingResultMessage> errorLog,
                                  MatchingResultMessage message) {
    return errorLog.contains(message);
  }
}
