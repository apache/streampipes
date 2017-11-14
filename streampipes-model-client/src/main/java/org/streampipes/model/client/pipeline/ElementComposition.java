package org.streampipes.model.client.pipeline;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


public abstract class ElementComposition {

	@OneToMany(cascade=CascadeType.ALL)
	protected List<DataProcessorInvocation> sepas;
	
	@OneToMany(cascade=CascadeType.ALL)
	protected List<SpDataStream> streams;
	
	protected String name;
	protected String description;

	public ElementComposition() {
		this.sepas = new ArrayList<>();
		this.streams = new ArrayList<>();
	}

	public List<DataProcessorInvocation> getSepas() {
		return sepas;
	}

	public void setSepas(List<DataProcessorInvocation> sepas) {
		this.sepas = sepas;
	}

	public List<SpDataStream> getStreams() {
		return streams;
	}

	public void setStreams(List<SpDataStream> streams) {
		this.streams = streams;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
