package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass("sepa:EventPropertyQualityRequirement")
@MappedSuperclass
@Entity
public class EventPropertyQualityRequirement extends UnnamedSEPAElement {

	private static final long serialVersionUID = -8173312776233284351L;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:minimumEventPropertyQuality")
	private transient EventPropertyQualityDefinition minimumPropertyQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:maximumEventPropertyQuality")
	private transient EventPropertyQualityDefinition maximumPropertyQuality;
	
	public EventPropertyQualityRequirement() {
		super();
	}

	public EventPropertyQualityRequirement(
			EventPropertyQualityDefinition minimumPropertyQuality,
			EventPropertyQualityDefinition  maximumPropertyQuality) {
		
		super();
		this.minimumPropertyQuality = minimumPropertyQuality;
		this.maximumPropertyQuality = maximumPropertyQuality;
	}
	
	public EventPropertyQualityRequirement(EventPropertyQualityRequirement other) {
		super(other);
		//this.minimumPropertyQuality = other.getMinimumPropertyQuality();
		//this.maximumPropertyQuality = other.getMaximumPropertyQuality();
	}

	public EventPropertyQualityDefinition getMinimumPropertyQuality() {
		return minimumPropertyQuality;
	}

	public void setMinimumPropertyQuality(
			EventPropertyQualityDefinition minimumPropertyQuality) {
		this.minimumPropertyQuality = minimumPropertyQuality;
	}

	public EventPropertyQualityDefinition getMaximumPropertyQuality() {
		return maximumPropertyQuality;
	}

	public void setMaximumPropertyQuality(
			EventPropertyQualityDefinition maximumPropertyQuality) {
		this.maximumPropertyQuality = maximumPropertyQuality;
	}
}
