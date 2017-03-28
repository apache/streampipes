package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.client.matching.MatchingResultType;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;

public class StreamQualityMatch extends AbstractMatcher<EventStreamQualityDefinition, EventStreamQualityRequirement> {

	public StreamQualityMatch() {
		super(MatchingResultType.STREAM_QUALITY);
	}

	@Override
	public boolean match(EventStreamQualityDefinition offer,
			EventStreamQualityRequirement requirement, List<MatchingResultMessage> errorLog) {
		
		// TODO
		boolean match = MatchingUtils.nullCheck(offer, requirement);
		return true;
				
	}

}
