package de.fzi.cep.sepa.model.client;

import java.util.List;

public class SourceClient extends SEPAElement {

	private List<String> domain;

	public SourceClient(String name, String description, List<String> domain)
	{
		super(name, description);
		this.domain = domain;
	}
	
	public SourceClient(String name, String description, List<String> domain, String iconName)
	{
		super(name, description, iconName);
		this.domain = domain;
	}

	public List<String> getDomain() {
		return domain;
	}

	public void setDomain(List<String> domain) {
		this.domain = domain;
	}
	
	
}
