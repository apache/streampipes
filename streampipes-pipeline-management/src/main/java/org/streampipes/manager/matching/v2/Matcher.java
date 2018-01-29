package org.streampipes.manager.matching.v2;

import java.util.List;

import org.streampipes.model.client.matching.MatchingResultMessage;

public interface Matcher<L, R> {

	boolean match(L offer, R requirement, List<MatchingResultMessage> errorLog);
}
