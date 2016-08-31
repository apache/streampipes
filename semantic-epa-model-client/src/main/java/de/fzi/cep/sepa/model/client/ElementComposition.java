package de.fzi.cep.sepa.model.client;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.List;


public abstract class ElementComposition {

	@OneToMany(cascade=CascadeType.ALL)
	protected List<SepaInvocation> sepas;
	
	@OneToMany(cascade=CascadeType.ALL)
	protected List<EventStream> streams;
	
	protected String name;
	protected String description;
	
	protected String pipelineId;
	//protected String rev;
	
	public List<SepaInvocation> getSepas() {
		return sepas;
	}

	public void setSepas(List<SepaInvocation> sepas) {
		this.sepas = sepas;
	}

	public List<EventStream> getStreams() {
		return streams;
	}

	public void setStreams(List<EventStream> streams) {
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

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

//	public String getRev() {
//		return rev;
//	}
//
//	public void setRev(String rev) {
//		this.rev = rev;
//	}
}
