package org.streampipes.manager.matching.v2;

import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.impl.TransportProtocol;

public class ProtocolMatch extends AbstractMatcher<TransportProtocol, TransportProtocol>{

	public ProtocolMatch() {
		super(MatchingResultType.PROTOCOL_MATCH);
	}

	@Override
	public boolean match(TransportProtocol offer, TransportProtocol requirement, List<MatchingResultMessage> errorLog) {
		
		boolean matches = MatchingUtils.nullCheck(offer, requirement) ||
				canonicalName(requirement).equals(canonicalName(offer));
		
		if (!matches) buildErrorMessage(errorLog, "protocol");
		return matches;
	}
	
	private String canonicalName(TransportProtocol protocol) {
		return protocol.getClass().getCanonicalName();
	}

}
