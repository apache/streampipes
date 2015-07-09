package de.fzi.cep.sepa.model.impl.output;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:AppendOutputStrategy")
@Entity
public class AppendOutputStrategy extends OutputStrategy {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:appendsProperty")
	List<EventProperty> eventProperties;
	
	public AppendOutputStrategy()
	{
		super();
		eventProperties = new ArrayList<EventProperty>();
	}
	
	public AppendOutputStrategy(List<EventProperty> eventProperties) {
		super();
		this.eventProperties = eventProperties;
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
	
}
