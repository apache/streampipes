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
		boolean matches =  MatchingUtils.nullCheck(offer, requirement) ||
				(unitMatch(offer.getMeasurementUnit(), requirement.getMeasurementUnit(), errorLog) &&
						datatypeMatch(offer.getRuntimeType(), requirement.getRuntimeType(), errorLog) &&
						domainPropertyMatch(offer.getDomainProperties(), requirement.getDomainProperties(), errorLog));
		
		//if (!matches) buildErrorMessage(errorLog, buildText(requirement));
		return matches;
	}

	private String buildText(EventPropertyPrimitive requirement) {
		if (requirement == null || requirement.getDomainProperties() == null) return "-";
		return "Required domain properties: " +requirement.getDomainProperties().size() +", "
				+"required data type: " +requirement.getRuntimeType();
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
