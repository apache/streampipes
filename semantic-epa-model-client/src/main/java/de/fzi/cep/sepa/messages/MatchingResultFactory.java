package de.fzi.cep.sepa.messages;

public class MatchingResultFactory {

	public static MatchingResultMessage build(MatchingResultType type, boolean success, String rightSubject) {
		MatchingResultMessage message = new MatchingResultMessage();
		message.setMatchingSuccessful(success);
		message.setTitle(type.getTitle());
		message.setDescription(type.getDescription());
		message.setRequirementSubject(rightSubject);
		return message;
	}
}
