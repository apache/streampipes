package org.streampipes.model.client.connection;

public class Connection {

	private String from;
	private String to;
	
	public Connection(String from, String to)
	{
		this.from = from;
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
}
