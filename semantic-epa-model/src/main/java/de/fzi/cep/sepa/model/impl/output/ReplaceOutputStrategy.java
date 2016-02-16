package de.fzi.cep.sepa.model.impl.output;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:ReplaceOutputStrategy")
@Entity
public class ReplaceOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 1L;

	@RdfProperty("sepa:replaceFrom")
	private URI replaceFrom;
	
	@RdfProperty("sepa:replaceWith")
	private DomainStaticProperty replaceWith;
	
	public ReplaceOutputStrategy() {
		super();
		this.replaceWith = new DomainStaticProperty();
		//List<SupportedProperty> supportedProperties = new ArrayList<>();
		//supportedProperties.add(new SupportedProperty)
	}
	
	public ReplaceOutputStrategy(ReplaceOutputStrategy other) {
		super(other);
		
	}

	public URI getReplaceFrom() {
		return replaceFrom;
	}

	public void setReplaceFrom(URI replaceFrom) {
		this.replaceFrom = replaceFrom;
	}
	
}
