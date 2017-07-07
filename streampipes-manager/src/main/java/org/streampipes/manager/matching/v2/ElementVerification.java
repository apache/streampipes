package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaInvocation;

public class ElementVerification {

	private List<MatchingResultMessage> errorLog;
	
	public ElementVerification() {
		this.errorLog = new ArrayList<>();
	}
		
	public boolean verify(SepaInvocation offer, InvocableSEPAElement requirement) {
		return new StreamMatch()
			.matchIgnoreGrounding(offer.getOutputStream(), 
					requirement.getStreamRequirements().get(0), errorLog) &&
			new GroundingMatch().match(offer.getSupportedGrounding(), requirement.getSupportedGrounding(), errorLog);
	}

	public boolean verify(EventStream offer, InvocableSEPAElement requirement) {
		return new StreamMatch().matchIgnoreGrounding(offer, requirement.getStreamRequirements().get(0), errorLog) &&
			new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
	}
	
	public List<MatchingResultMessage> getErrorLog() {
		return errorLog;
	}
}
