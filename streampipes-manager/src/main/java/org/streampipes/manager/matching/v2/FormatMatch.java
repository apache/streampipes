package org.streampipes.manager.matching.v2;

import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.impl.TransportFormat;

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
