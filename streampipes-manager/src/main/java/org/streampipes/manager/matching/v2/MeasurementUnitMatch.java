package org.streampipes.manager.matching.v2;

import java.net.URI;
import java.util.List;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;

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
