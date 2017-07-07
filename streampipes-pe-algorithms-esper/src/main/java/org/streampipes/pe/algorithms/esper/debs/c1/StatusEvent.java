package org.streampipes.pe.algorithms.esper.debs.c1;

public class StatusEvent {

	private boolean start;
	private long timestamp;
	private long eventCount;
	
	
	public StatusEvent(boolean start, long timestamp, long eventCount) {
		super();
		this.start = start;
		this.timestamp = timestamp;
		this.eventCount = eventCount;
	}
	
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getEventCount() {
		return eventCount;
	}

	public void setEventCount(long eventCount) {
		this.eventCount = eventCount;
	}
	
	
	
}
