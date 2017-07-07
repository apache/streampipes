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
import de.fzi.cep.sepa.model.util.Cloner;

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

	@RdfProperty("sepa:outputRight")
	protected boolean outputRight;

    private List<EventProperty> providesProperties;
	
	public CustomOutputStrategy()
	{
		super();
		this.eventProperties = new ArrayList<>();
	}
	
	public CustomOutputStrategy(boolean outputRight) {
		super();
		this.outputRight = outputRight;
		this.eventProperties = new ArrayList<>();
	}
	
	public CustomOutputStrategy(CustomOutputStrategy other) {
		super(other);
		this.eventProperties = new Cloner().properties(other.getEventProperties());
		this.outputRight = other.isOutputRight();
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

	public boolean isOutputRight() {
		return outputRight;
	}

	public void setOutputRight(boolean outputRight) {
		this.outputRight = outputRight;
	}

    public List<EventProperty> getProvidesProperties() {
        return providesProperties;
    }

    public void setProvidesProperties(List<EventProperty> providesProperties) {
        this.providesProperties = providesProperties;
    }
}