package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY_QUALITY_REQUIREMENT)
@MappedSuperclass
@Entity
public class EventPropertyQualityRequirement extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = -8173312776233284351L;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.MINIMUM_EVENT_PROPERTY_QUALITY)
	private transient EventPropertyQualityDefinition minimumPropertyQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.MAXIMUM_EVENT_PROPERTY_QUALITY)
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
