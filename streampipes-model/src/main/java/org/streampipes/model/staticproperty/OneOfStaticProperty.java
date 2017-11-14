package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.ONE_OF_STATIC_PROPERTY)
@Entity
public class OneOfStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 3483290363677184344L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_OPTION)
	private List<Option> options;
	
	
	public OneOfStaticProperty() {
		super(StaticPropertyType.OneOfStaticProperty);
		options = new ArrayList<>();
	}
	
	public OneOfStaticProperty(OneOfStaticProperty other) {
		super(other);
		this.options = new Cloner().options(other.getOptions());
	}

	public OneOfStaticProperty(String internalName, String label, String description) {
		super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
		options = new ArrayList<>();
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
