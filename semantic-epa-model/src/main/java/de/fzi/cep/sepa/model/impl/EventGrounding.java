package de.fzi.cep.sepa.model.impl;

public class EventGrounding{

	private TransportProtocol transportProtocol;
	
	private int port;
	
	private String uri;
	
	private TransportFormat transportFormat;
	
	public EventGrounding(TransportProtocol transportProtocol, int port, String uri, TransportFormat transportFormat)
	{
		this.transportFormat = transportFormat;
		this.transportProtocol = transportProtocol;
		this.uri = uri;
		this.port = port;
	}

	public TransportProtocol getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public TransportFormat getTransportFormat() {
		return transportFormat;
	}

	public void setTransportFormat(TransportFormat transportFormat) {
		this.transportFormat = transportFormat;
	}
	
	
	
}
