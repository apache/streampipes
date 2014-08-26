package de.fzi.cep.sepa.model.impl;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:AnyStaticProperty")
@Entity
public class AnyStaticProperty extends StaticProperty {
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOptions")
	List<Option> options;
	
	
	public AnyStaticProperty() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnyStaticProperty(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}	
}
