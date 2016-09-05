package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;

public interface Matcher<L, R> {

	public boolean match(L offer, R requirement, List<MatchingResultMessage> errorLog);
}
