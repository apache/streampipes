package org.streampipes.model.client.exception;

import java.util.List;

import org.streampipes.model.client.matching.MatchingResultMessage;

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
