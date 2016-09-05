package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;

public class ListPropertyMatch implements Matcher<EventPropertyList, EventPropertyList> {

	@Override
	public boolean match(EventPropertyList offer, EventPropertyList requirement, List<MatchingResultMessage> errorLog) {
		// TODO Auto-generated method stub
		return false;
	}

}
