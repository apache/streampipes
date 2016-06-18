package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;

public class NestedPropertyMatch implements Matcher<EventPropertyNested, EventPropertyNested>{

	@Override
	public boolean match(EventPropertyNested offer,
			EventPropertyNested requirement, List<MatchingResultMessage> errorLog) {
		// TODO Auto-generated method stub
		return false;
	}

}
