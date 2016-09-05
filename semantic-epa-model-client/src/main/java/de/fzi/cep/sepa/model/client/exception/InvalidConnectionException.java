package de.fzi.cep.sepa.model.client.exception;

import java.util.List;

import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;

public class InvalidConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	private List<MatchingResultMessage> errorLog;
	
	public InvalidConnectionException(List<MatchingResultMessage> errorLog) {
		this.errorLog = errorLog;
	}
	
	public List<MatchingResultMessage> getErrorLog() {
		return errorLog;
	}
}
