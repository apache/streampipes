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
