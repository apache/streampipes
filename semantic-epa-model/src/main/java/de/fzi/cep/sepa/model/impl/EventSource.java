package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventSource")
@Entity
public class EventSource extends NamedSEPAElement {

	public EventSource()
	{
		super();
	}
	
	public EventSource(String uri, String name, String description) {
		super(uri, name, description);
		// TODO Auto-generated constructor stub
	}


	
}
