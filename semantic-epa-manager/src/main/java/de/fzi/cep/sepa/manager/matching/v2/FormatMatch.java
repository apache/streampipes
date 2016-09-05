package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.client.matching.MatchingResultType;
import de.fzi.cep.sepa.model.impl.TransportFormat;

public class FormatMatch extends AbstractMatcher<TransportFormat, TransportFormat>{

	public FormatMatch() {
		super(MatchingResultType.FORMAT_MATCH);
	}

	@Override
	public boolean match(TransportFormat offer, TransportFormat requirement, List<MatchingResultMessage> errorLog) {
		return MatchingUtils.nullCheck(offer, requirement) ||
				requirement.getRdfType().containsAll(offer.getRdfType());
		
	}

}
