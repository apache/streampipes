package de.fzi.cep.sepa.manager.matching.v2;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;

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
