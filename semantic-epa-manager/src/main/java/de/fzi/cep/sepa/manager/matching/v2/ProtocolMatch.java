package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.messages.MatchingResultType;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

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
