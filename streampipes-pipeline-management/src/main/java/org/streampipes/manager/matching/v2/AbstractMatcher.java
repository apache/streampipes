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

import java.util.List;

import org.streampipes.model.client.matching.MatchingResultFactory;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;

public abstract class AbstractMatcher<L, R> implements Matcher<L, R>{

	protected MatchingResultType matchingResultType;
	
	public AbstractMatcher(MatchingResultType matchingResultType) {
		this.matchingResultType = matchingResultType;
	}
	
	protected void buildErrorMessage(List<MatchingResultMessage> errorLog, String rightSubject) {
		errorLog.add(MatchingResultFactory.build(matchingResultType, false, rightSubject));
	}
	
	protected void buildErrorMessage(List<MatchingResultMessage> errorLog, MatchingResultType type, String rightSubject) {
		errorLog.add(MatchingResultFactory.build(type, false, rightSubject));
	}
		
	public abstract boolean match(L offer, R requirement, List<MatchingResultMessage> errorLog);
}
