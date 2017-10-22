package org.streampipes.model.impl;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.NamedSEPAElement;

import javax.persistence.Entity;

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
