package de.fzi.cep.sepa.esper.debs.c1;

public class DebsOutputParameters {
	
	private String path;
	
	public DebsOutputParameters(String path)
	{
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
