package de.fzi.cep.sepa.model.client;

import java.util.List;

public class SourceClient extends SEPAElement {

	private List<Domain> domain;

	public SourceClient(String name, String description, List<Domain> domain)
	{
		super(name, description);
		this.domain = domain;
	}
	
	public SourceClient(String name, String description, List<Domain> domain, String iconName)
	{
		super(name, description, iconName);
		this.domain = domain;
	}

	public List<Domain> getDomain() {
		return domain;
	}

	public void setDomain(List<Domain> domain) {
		this.domain = domain;
	}
	
	
}
