package de.fzi.cep.sepa.model.impl.quality;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "ssn",   "http://purl.oclc.org/NET/ssnx/ssn#"})
@RdfsClass("sepa:EventStreamQualityDefinition")
@MappedSuperclass
@Entity
public class EventStreamQualityDefinition extends MeasurementProperty {

	private static final long serialVersionUID = 6310763356941481868L;

	public EventStreamQualityDefinition() {
		super();
	}

	public EventStreamQualityDefinition(EventStreamQualityDefinition o) {
		super(o);
	}


}