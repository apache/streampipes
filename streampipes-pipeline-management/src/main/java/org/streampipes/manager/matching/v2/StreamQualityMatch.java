package org.streampipes.manager.matching.v2;

import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.quality.EventStreamQualityRequirement;

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
