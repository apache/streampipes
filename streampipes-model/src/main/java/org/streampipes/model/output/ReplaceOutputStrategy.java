package org.streampipes.model.output;

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

@RdfsClass(StreamPipes.REPLACE_OUTPUT_STRATEGY)
@Entity
public class ReplaceOutputStrategy extends OutputStrategy {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.REPLACES_PROPERTY)
	private List<UriPropertyMapping> replaceProperties;
	
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
