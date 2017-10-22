package org.streampipes.model.impl.eventproperty;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;


@RdfsClass("so:Enumeration")
@Entity
public class Enumeration extends ValueSpecification {

	private static final long serialVersionUID = 1L;
	
	@RdfProperty("rdfs:label")
	protected String label;
	
	@RdfProperty("rdfs:description")
	protected String description;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasRuntimeValue")
	List<String> runtimeValues;

	public Enumeration() {
		super();
	}

	public Enumeration(String label, String description, List<String> runtimeValues) {
		super();
		this.label = label;
		this.description = description;
		this.runtimeValues = runtimeValues;
	}
	
	public Enumeration(Enumeration other) {
		super(other);
		this.label = other.getLabel();
		this.description = other.getDescription();
		this.runtimeValues = other.getRuntimeValues();
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getRuntimeValues() {
		return runtimeValues;
	}

	public void setRuntimeValues(List<String> runtimeValues) {
		this.runtimeValues = runtimeValues;
	}
	
	
}
