package de.fzi.cep.sepa.manager.matching.v2;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.messages.MatchingResultType;

public class MeasurementUnitMatch extends AbstractMatcher<URI, URI> {

	public MeasurementUnitMatch() {
		super(MatchingResultType.MEASUREMENT_UNIT_MATCH);
	}

	@Override
	public boolean match(URI offer, URI requirement, List<MatchingResultMessage> errorLog) {
		// TODO check with knowledge base
//		return MatchingUtils.nullCheck(offer, requirement) ||
//				requirement.toString().equals(offer.toString());
		return true;
	}

}
