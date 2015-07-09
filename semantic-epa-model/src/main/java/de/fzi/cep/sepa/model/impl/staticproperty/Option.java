package de.fzi.cep.sepa.model.impl.staticproperty;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:Option")
@Entity
public class Option extends UnnamedSEPAElement {

	@RdfProperty("sepa:hasName")
	String name;
	
	@RdfProperty("sepa:isSelected")
	boolean selected;
	
	public Option()
	{
		super();
	}
	
	public Option(String name)
	{
		super();
		this.name = name;
	}
	
	public Option(String name, boolean selected)
	{
		super();
		this.name = name;
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
