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

import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:OneOfStaticProperty")
@Entity
public class OneOfStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 3483290363677184344L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOption")
	List<Option> options;
	
	
	public OneOfStaticProperty() {
		super();
		options = new ArrayList<Option>();
	}
	
	public OneOfStaticProperty(OneOfStaticProperty other) {
		super(other);
		this.options = new Cloner().options(other.getOptions());
	}

	public OneOfStaticProperty(String internalName, String label, String description) {
		super(internalName, label, description);
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
	
	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
}
