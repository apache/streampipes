package de.fzi.cep.sepa.model.client;

public class StreamClient extends SEPAElement {

	private String sourceId;
	
	public StreamClient(String name, String description, String sourceId)
	{
		super(name, description);
		this.sourceId = sourceId;
	}
	
	public StreamClient(String name, String description, String sourceId, String iconName)
	{
		super(name, description, iconName);
		this.sourceId = sourceId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	
	
	
}
