package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.ANY_STATIC_PROPERTY)
@Entity
public class AnyStaticProperty extends StaticProperty {
	
	private static final long serialVersionUID = -7046019539598560494L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_OPTION)
	private List<Option> options;
	
	
	public AnyStaticProperty() {
		super(StaticPropertyType.AnyStaticProperty);
	}
	
	public AnyStaticProperty(AnyStaticProperty other) {
		super(other);
		this.options = new Cloner().options(other.getOptions());
	}

	public AnyStaticProperty(String internalName, String label, String description) {
		super(StaticPropertyType.AnyStaticProperty, internalName, label, description);
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

}
