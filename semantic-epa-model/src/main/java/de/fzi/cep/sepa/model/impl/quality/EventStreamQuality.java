package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:EventStreamQuality")
@MappedSuperclass
@Entity
public class EventStreamQuality extends MeasurementProperty {


	public EventStreamQuality() {
		super();
	}


}