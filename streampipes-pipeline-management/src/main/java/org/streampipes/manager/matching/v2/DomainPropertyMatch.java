package org.streampipes.manager.matching.v2;

import java.net.URI;
import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;

public class DomainPropertyMatch extends AbstractMatcher<List<URI>, List<URI>> {

	public DomainPropertyMatch() {
		super(MatchingResultType.DOMAIN_PROPERTY_MATCH);
	}

	@Override
	public boolean match(List<URI> offer, List<URI> requirement, List<MatchingResultMessage> errorLog) {
		boolean match = MatchingUtils.nullCheck(offer, requirement) ||
				requirement
				.stream()
				.allMatch(req -> offer
						.stream()
						.anyMatch(of -> req
								.toString()
								.equals(of.toString())));
		
		if (!match) buildErrorMessage(errorLog, buildText(requirement));
		return match;
	}

	private String buildText(List<URI> requirement) {
		if (requirement == null || requirement.size() == 0) return "-";
		else return "Required domain property: " +requirement.get(0).toString();
	}

}
