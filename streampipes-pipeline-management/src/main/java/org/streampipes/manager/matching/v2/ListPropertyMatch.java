package org.streampipes.manager.matching.v2;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.schema.EventPropertyList;

import java.util.List;

public class ListPropertyMatch implements Matcher<EventPropertyList, EventPropertyList> {

	@Override
	public boolean match(EventPropertyList offer, EventPropertyList requirement, List<MatchingResultMessage> errorLog) {
		if (requirement.getEventProperties() == null || requirement.getEventProperties().size() == 0) {
			return false;
		} else {
			return new PropertyMatch().match(offer.getEventProperties().get(0),
							requirement.getEventProperties().get(0), errorLog);
		}
	}
}
