package org.streampipes.model.impl.output;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.util.Cloner;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:FixedOutputStrategy")
@Entity
public class FixedOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 812840089727019773L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:producesProperty")
	List<EventProperty> eventProperties;


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