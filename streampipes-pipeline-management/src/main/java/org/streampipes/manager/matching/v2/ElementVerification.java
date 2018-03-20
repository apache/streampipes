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

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;

public class ElementVerification {

	private List<MatchingResultMessage> errorLog;
	
	public ElementVerification() {
		this.errorLog = new ArrayList<>();
	}
		
	public boolean verify(DataProcessorInvocation offer, InvocableStreamPipesEntity requirement) {
		return new StreamMatch()
			.matchIgnoreGrounding(offer.getOutputStream(), 
					requirement.getStreamRequirements().get(0), errorLog) &&
			new GroundingMatch().match(offer.getSupportedGrounding(), requirement.getSupportedGrounding(), errorLog);
	}

	public boolean verify(SpDataStream offer, InvocableStreamPipesEntity requirement) {
		return new StreamMatch().matchIgnoreGrounding(offer, requirement.getStreamRequirements().get(0), errorLog) &&
			new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
	}
	
	public List<MatchingResultMessage> getErrorLog() {
		return errorLog;
	}
}
