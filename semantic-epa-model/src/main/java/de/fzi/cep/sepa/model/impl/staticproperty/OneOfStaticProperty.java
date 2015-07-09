package de.fzi.cep.sepa.model.impl.staticproperty;

import java.util.ArrayList;
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
@RdfsClass("sepa:OneOfStaticProperty")
@Entity
public class OneOfStaticProperty extends StaticProperty {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOption")
	List<Option> options;
	
	
	public OneOfStaticProperty() {
		super();
		options = new ArrayList<Option>();
	}

	public OneOfStaticProperty(String name, String description) {
		super(name, description);
		options = new ArrayList<Option>();
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}	
	
	public boolean addOption(Option option)
	{
		return options.add(option);
	}
}
