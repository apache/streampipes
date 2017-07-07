package org.streampipes.manager.matching.v2;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;

import java.util.List;

public class NestedPropertyMatch implements Matcher<EventPropertyNested, EventPropertyNested>{

	@Override
	public boolean match(EventPropertyNested offer,
			EventPropertyNested requirement, List<MatchingResultMessage> errorLog) {
		return requirement
						.getEventProperties()
						.stream()
						.allMatch(r -> offer
										.getEventProperties()
										.stream()
										.anyMatch(of -> new PropertyMatch().match(of, r, errorLog)));
	}

}
