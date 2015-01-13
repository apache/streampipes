package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class StreamDescription extends Description {

	List<AgentDescription> streams;
	
	public StreamDescription(String name, String description, URI uri, List<AgentDescription> streams)
	{
		this.name = name;
		this.uri = uri;
		this.streams = streams;
	}
	
	public StreamDescription()
	{
		streams = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<AgentDescription> getStreams() {
		return streams;
	}

	public void setStreams(List<AgentDescription> streams) {
		this.streams = streams;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
}
