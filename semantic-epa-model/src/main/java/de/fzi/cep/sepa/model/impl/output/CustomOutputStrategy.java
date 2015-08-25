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
@RdfsClass("sepa:CustomOutputStrategy")
@Entity
public class CustomOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = -5858193127308435472L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:producesProperty")
	List<EventProperty> eventProperties;
	
	public CustomOutputStrategy()
	{
		super();
		this.eventProperties = new ArrayList<>();
	}
	
	public CustomOutputStrategy(List<EventProperty> eventProperties)
	{
		this.eventProperties = eventProperties;
	}

	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
}