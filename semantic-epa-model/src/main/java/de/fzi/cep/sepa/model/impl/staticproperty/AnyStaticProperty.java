package de.fzi.cep.sepa.model.impl.staticproperty;

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
@RdfsClass("sepa:AnyStaticProperty")
@Entity
public class AnyStaticProperty extends StaticProperty {
	
	private static final long serialVersionUID = -7046019539598560494L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOption")
	List<Option> options;
	
	
	public AnyStaticProperty() {
		super(StaticPropertyType.AnyStaticProperty);
		// TODO Auto-generated constructor stub
	}
	
	public AnyStaticProperty(AnyStaticProperty other) {
		super(other);
		this.options = new Cloner().options(other.getOptions());
	}

	public AnyStaticProperty(String internalName, String label, String description) {
		super(StaticPropertyType.AnyStaticProperty, internalName, label, description);
		// TODO Auto-generated constructor stub
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
}
