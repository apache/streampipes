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
@RdfsClass("sepa:RequiresEventStreamQuality")
@MappedSuperclass
@Entity
public class RequiresEventStreamQuality extends UnnamedSEPAElement {

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:minimumEventStreamQuality")
	EventStreamQuality minimumStreamQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:maximumEventStreamQuality")
	EventStreamQuality maximumStreamQuality;

	public RequiresEventStreamQuality(EventStreamQuality minimumStreamQuality,
			EventStreamQuality maximumStreamQuality) {
		super();
		//TODO check that minimum and maximum have the same type

		this.minimumStreamQuality = minimumStreamQuality;
		this.maximumStreamQuality = maximumStreamQuality;
	}
	
	public RequiresEventStreamQuality() {
		super();
	}

	public EventStreamQuality getMinimumStreamQuality() {
		return minimumStreamQuality;
	}

	public void setMinimumStreamQuality(EventStreamQuality minimumStreamQuality) {
		this.minimumStreamQuality = minimumStreamQuality;
	}

	public EventStreamQuality getMaximumStreamQuality() {
		return maximumStreamQuality;
	}

	public void setMaximumStreamQuality(EventStreamQuality maximumStreamQuality) {
		this.maximumStreamQuality = maximumStreamQuality;
	}
}
