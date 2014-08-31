package de.fzi.cep.sepa.model.impl.graph;

import java.util.List;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.EventStream;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventConsumer")
@Entity
public class SEC extends NamedSEPAElement{

	public SEC(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
	}
	
	public SEC()
	{
		super();
	}
}
