package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.FIXED_OUTPUT_STRATEGY)
@Entity
public class FixedOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 812840089727019773L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.PRODUCES_PROPERTY)
	private List<EventProperty> eventProperties;

	public FixedOutputStrategy()
	{
		super();
	}
	
	public FixedOutputStrategy(FixedOutputStrategy other) {
		super(other);
		this.eventProperties = new Cloner().properties(other.getEventProperties());
	}
	
	public FixedOutputStrategy(List<EventProperty> eventProperties)
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