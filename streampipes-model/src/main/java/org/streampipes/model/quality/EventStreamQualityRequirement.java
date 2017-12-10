package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.Event_STREAM_QUALITY_REQUIREMENT)
@MappedSuperclass
@Entity
public class EventStreamQualityRequirement extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 1484115035721357275L;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.MINIMUM_EVENT_STREAM_QUALITY)
	private transient EventStreamQualityDefinition minimumStreamQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.MAXIMUM_EVENT_STREAM_QUALITY)
	private transient EventStreamQualityDefinition maximumStreamQuality;

	public EventStreamQualityRequirement(EventStreamQualityDefinition minimumStreamQuality,
			EventStreamQualityDefinition maximumStreamQuality) {
		super();
		//TODO check that minimum and maximum have the same type
		this.minimumStreamQuality = minimumStreamQuality;
		this.maximumStreamQuality = maximumStreamQuality;
	}
	
	public EventStreamQualityRequirement(EventStreamQualityRequirement other) {
		super(other);
		//this.minimumStreamQuality = other.getMinimumStreamQuality();
		//this.maximumStreamQuality = other.getMaximumStreamQuality();
	}
	
	public EventStreamQualityRequirement() {
		super();
	}

	public EventStreamQualityDefinition getMinimumStreamQuality() {
		return minimumStreamQuality;
	}

	public void setMinimumStreamQuality(EventStreamQualityDefinition minimumStreamQuality) {
		this.minimumStreamQuality = minimumStreamQuality;
	}

	public EventStreamQualityDefinition getMaximumStreamQuality() {
		return maximumStreamQuality;
	}

	public void setMaximumStreamQuality(EventStreamQualityDefinition maximumStreamQuality) {
		this.maximumStreamQuality = maximumStreamQuality;
	}
}
