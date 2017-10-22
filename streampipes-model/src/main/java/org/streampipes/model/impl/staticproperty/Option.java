package org.streampipes.model.impl.staticproperty;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;

@RdfsClass("sepa:Option")
@Entity
public class Option extends UnnamedSEPAElement {
	
	private static final long serialVersionUID = 8536995294188662931L;

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

	public Option(Option o) {
		super(o);
		this.name = o.getName();
		this.selected = o.isSelected();
		
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
