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
@RdfsClass("sepa:RequiresEventPropertyQuality")
@MappedSuperclass
@Entity
public class RequiresEventPropertyQuality extends UnnamedSEPAElement {

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:minimumEventPropertyQuality")
	EventPropertyQuality minimumPropertyQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:maximumEventPropertyQuality")
	EventPropertyQuality maximumPropertyQuality;
	
	public RequiresEventPropertyQuality() {
		super();
	}

	public RequiresEventPropertyQuality(
			EventPropertyQuality minimumPropertyQuality,
			EventPropertyQuality  maximumPropertyQuality) {
		
		super();
		this.minimumPropertyQuality = minimumPropertyQuality;
		this.maximumPropertyQuality = maximumPropertyQuality;
	}

	public EventPropertyQuality getMinimumPropertyQuality() {
		return minimumPropertyQuality;
	}

	public void setMinimumPropertyQuality(
			EventPropertyQuality minimumPropertyQuality) {
		this.minimumPropertyQuality = minimumPropertyQuality;
	}

	public EventPropertyQuality getMaximumPropertyQuality() {
		return maximumPropertyQuality;
	}

	public void setMaximumPropertyQuality(
			EventPropertyQuality maximumPropertyQuality) {
		this.maximumPropertyQuality = maximumPropertyQuality;
	}
	
	
}
