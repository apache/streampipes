package org.streampipes.pe.sinks.standalone.samples.evaluation;

public class ReceivedEvent {

	private byte[] byteMsg;
	private long timestamp;
	
	
	public ReceivedEvent(byte[] byteMsg, long timestamp) {
		super();
		this.byteMsg = byteMsg;
		this.timestamp = timestamp;
	}
	
	public byte[] getByteMsg() {
		return byteMsg;
	}
	public void setByteMsg(byte[] byteMsg) {
		this.byteMsg = byteMsg;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
