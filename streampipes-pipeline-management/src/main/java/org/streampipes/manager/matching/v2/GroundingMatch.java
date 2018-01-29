package org.streampipes.manager.matching.v2;

import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

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
