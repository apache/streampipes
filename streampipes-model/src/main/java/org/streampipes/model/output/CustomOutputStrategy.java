package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.CUSTOM_OUTPUT_STRATEGY)
@Entity
public class CustomOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = -5858193127308435472L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.PRODUCES_PROPERTY)
	private List<EventProperty> eventProperties;

	@RdfProperty(StreamPipes.OUTPUT_RIGHT)
	private boolean outputRight;

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