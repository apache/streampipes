package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class ElementVerification {

	private List<MatchingResultMessage> errorLog;
	
	public ElementVerification() {
		this.errorLog = new ArrayList<>();
	}
		
	public boolean verify(SepaInvocation offer, ConsumableSEPAElement requirement) {
		return new StreamMatch()
			.matchIgnoreGrounding(offer.getOutputStream(), 
					requirement.getEventStreams().get(0), errorLog) &&
			new GroundingMatch().match(offer.getSupportedGrounding(), requirement.getSupportedGrounding(), errorLog);
	}

	public boolean verify(EventStream offer, ConsumableSEPAElement requirement) {
		return new StreamMatch().matchIgnoreGrounding(offer, requirement.getEventStreams().get(0), errorLog) && 
			new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
	}
	
	public List<MatchingResultMessage> getErrorLog() {
		return errorLog;
	}
}
