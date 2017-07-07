package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventSource")
@Entity
public class EventSource extends NamedSEPAElement {
	
	private static final long serialVersionUID = -6144439857250547201L;

	public EventSource()
	{
		super();
	}
	
	public EventSource(String uri, String name, String description, String iconUrl) {
		super(uri, name, description, iconUrl);
		// TODO Auto-generated constructor stub
	}

	

	

	
}
