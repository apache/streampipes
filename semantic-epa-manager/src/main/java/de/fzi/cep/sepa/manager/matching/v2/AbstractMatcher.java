package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.messages.MatchingResultFactory;
import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.messages.MatchingResultType;

public abstract class AbstractMatcher<L, R> implements Matcher<L, R>{

	protected MatchingResultType matchingResultType;
	
	public AbstractMatcher(MatchingResultType matchingResultType) {
		this.matchingResultType = matchingResultType;
	}
	
	protected void buildErrorMessage(List<MatchingResultMessage> errorLog, String rightSubject) {
		errorLog.add(MatchingResultFactory.build(matchingResultType, false, rightSubject));
	}
	
	protected void buildErrorMessage(List<MatchingResultMessage> errorLog, MatchingResultType type, String rightSubject) {
		errorLog.add(MatchingResultFactory.build(type, false, rightSubject));
	}
		
	public abstract boolean match(L offer, R requirement, List<MatchingResultMessage> errorLog);
}
