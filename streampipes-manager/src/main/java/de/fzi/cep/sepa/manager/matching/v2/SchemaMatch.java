package de.fzi.cep.sepa.manager.matching.v2;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.client.matching.MatchingResultType;
import de.fzi.cep.sepa.model.impl.EventSchema;

import java.util.List;

public class SchemaMatch extends AbstractMatcher<EventSchema, EventSchema>{

	public SchemaMatch() {
		super(MatchingResultType.SCHEMA_MATCH);
	}

	@Override
	public boolean match(EventSchema offer, EventSchema requirement, List<MatchingResultMessage> errorLog) {
		boolean matches = MatchingUtils.nullCheck(offer, requirement) ||
				requirement.getEventProperties()
				.stream()
				.allMatch(req -> offer
						.getEventProperties()
						.stream()
						.anyMatch(of -> new PropertyMatch().match(of, req, errorLog)));
		//if (!matches) buildErrorMessage(errorLog, builtText(requirement));
		return matches;
		
	}

	private String builtText(EventSchema requirement) {
		if (requirement == null || requirement.getEventProperties() == null) return "-";
		else return  "Required event properties: " +requirement.getEventProperties().size();
	}

}
