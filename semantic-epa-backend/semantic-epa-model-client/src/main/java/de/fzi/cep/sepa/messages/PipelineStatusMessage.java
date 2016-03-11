package de.fzi.cep.sepa.messages;

public class PipelineStatusMessage {

	private String pipelineId;
	private long timestamp;
	private String messageType;
	private String message;
	
	public PipelineStatusMessage(String pipelineId, long timestamp,
			String messageType, String message) {
		super();
		this.pipelineId = pipelineId;
		this.timestamp = timestamp;
		this.messageType = messageType;
		this.message = message;
	}
	
	public String getPipelineId() {
		return pipelineId;
	}
	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
