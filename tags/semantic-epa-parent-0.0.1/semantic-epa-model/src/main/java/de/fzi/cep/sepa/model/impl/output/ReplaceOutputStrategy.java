package de.fzi.cep.sepa.model.impl.output;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:ReplaceOutputStrategy")
@Entity
public class ReplaceOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:replacesProperty")
	protected List<UriPropertyMapping> replaceProperties;
	
	public ReplaceOutputStrategy() {
		super();
		this.replaceProperties = new ArrayList<>();
	}

	public ReplaceOutputStrategy(List<UriPropertyMapping> replaceProperties) {
		super();
		this.replaceProperties = replaceProperties;
	}
	
	public ReplaceOutputStrategy(ReplaceOutputStrategy other) {
		super(other);
		this.replaceProperties = new Cloner().replaceStrategy(other.getReplaceProperties());
		
	}

	public List<UriPropertyMapping> getReplaceProperties() {
		return replaceProperties;
	}

	public void setReplaceProperties(List<UriPropertyMapping> replaceProperties) {
		this.replaceProperties = replaceProperties;
	}


}
