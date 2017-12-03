package org.streampipes.manager.matching.v2;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;
import java.util.List;

public class PrimitivePropertyMatch extends AbstractMatcher<EventPropertyPrimitive, EventPropertyPrimitive>{

	public PrimitivePropertyMatch() {
		super(MatchingResultType.PROPERTY_MATCH);
	}

	@Override
	public boolean match(EventPropertyPrimitive offer,
			EventPropertyPrimitive requirement, List<MatchingResultMessage> errorLog) {

		boolean matchesUnit = unitMatch(offer.getMeasurementUnit(), requirement.getMeasurementUnit(), errorLog);
		boolean matchesDatatype = datatypeMatch(offer.getRuntimeType(), requirement.getRuntimeType(), errorLog);
		boolean matchesDomainProperty = domainPropertyMatch(offer.getDomainProperties(), requirement.getDomainProperties
						(), errorLog);

		return MatchingUtils.nullCheck(offer, requirement) ||
				(matchesUnit && matchesDatatype && matchesDomainProperty);
	}

	private boolean domainPropertyMatch(List<URI> offer,
			List<URI> requirement, List<MatchingResultMessage> errorLog) {
		return new DomainPropertyMatch().match(offer, requirement, errorLog);
	}

	private boolean datatypeMatch(String offer, String requirement,
			List<MatchingResultMessage> errorLog) {
		return new DatatypeMatch().match(offer, requirement, errorLog);
	}

	private boolean unitMatch(URI offer, URI requirement,
			List<MatchingResultMessage> errorLog) {
		return new MeasurementUnitMatch().match(offer, requirement, errorLog);
	}

}
