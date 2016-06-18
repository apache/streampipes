package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.messages.MatchingResultType;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class GroundingMatch extends AbstractMatcher<EventGrounding, EventGrounding>{

	public GroundingMatch() {
		super(MatchingResultType.GROUNDING_MATCH);
	}

	@Override
	public boolean match(EventGrounding offer, EventGrounding requirement, List<MatchingResultMessage> errorLog) {
		boolean match = MatchingUtils.nullCheckRightNullDisallowed(offer, requirement) ||
				(matchProtocols(offer.getTransportProtocols(), requirement.getTransportProtocols(), errorLog) &&
						matchFormats(offer.getTransportFormats(), requirement.getTransportFormats(), errorLog));
		
		return match;
		
	}
	
	private boolean matchProtocols(List<TransportProtocol> offer, List<TransportProtocol> requirement, List<MatchingResultMessage> errorLog) {
		boolean match = MatchingUtils.nullCheckBothNullDisallowed(offer, requirement) && 
				requirement
					.stream()
					.anyMatch(req -> offer.stream().anyMatch(of -> new ProtocolMatch().match(of, req, errorLog)));
		
		if (!match) buildErrorMessage(errorLog, MatchingResultType.PROTOCOL_MATCH, "Could not find matching protocol");
		return match;
	}
	
	private boolean matchFormats(List<TransportFormat> offer, List<TransportFormat> requirement, List<MatchingResultMessage> errorLog) {
		boolean match = MatchingUtils.nullCheckBothNullDisallowed(offer, requirement) && 
				requirement
					.stream()
					.anyMatch(req -> offer.stream().anyMatch(of -> new FormatMatch().match(of, req, errorLog)));
		
		if (!match) buildErrorMessage(errorLog, MatchingResultType.FORMAT_MATCH, "Could not find matching format");
		return match;
	}

}
