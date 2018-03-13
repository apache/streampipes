package org.streampipes.manager.matching.v2;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.List;

public class MeasurementUnitMatch extends AbstractMatcher<URI, URI> {

	public MeasurementUnitMatch() {
		super(MatchingResultType.MEASUREMENT_UNIT_MATCH);
	}

	@Override
	public boolean match(URI offer, URI requirement, List<MatchingResultMessage> errorLog) {
		return requirement == null || (requirement.toString().equals(StreamPipes.ANYTHING) && offer != null) ||
						(offer != null && requirement.toString().equals(offer.toString()));
	}

}
