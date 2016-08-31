package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:EventStreamQualityRequirement")
@MappedSuperclass
@Entity
public class EventStreamQualityRequirement extends UnnamedSEPAElement {

	private static final long serialVersionUID = 1484115035721357275L;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:minimumEventStreamQuality")
	transient EventStreamQualityDefinition minimumStreamQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:maximumEventStreamQuality")
	transient EventStreamQualityDefinition maximumStreamQuality;

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
