package de.fzi.cep.sepa.model.impl.output;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:RenameOutputStrategy")
@Entity
public class RenameOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 7643705399683055563L;
	
	@RdfProperty("sepa:eventName")
	String eventName;

	public RenameOutputStrategy()
	{
		super();
	}
	
	public RenameOutputStrategy(String name, String eventName) {
		super(name);
		this.eventName = eventName;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	
}
