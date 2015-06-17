package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import com.google.gson.annotations.SerializedName;

public abstract class ElementComposition {

	@OneToMany(cascade=CascadeType.ALL)
	protected List<SEPAClient> sepas;
	
	@OneToMany(cascade=CascadeType.ALL)
	protected List<StreamClient> streams;
	
	protected String name;
	protected String description;
	
	protected @SerializedName("_id") String pipelineId;
	protected @SerializedName("_rev") String rev;
	
	public List<SEPAClient> getSepas() {
		return sepas;
	}

	public void setSepas(List<SEPAClient> sepas) {
		this.sepas = sepas;
	}

	public List<StreamClient> getStreams() {
		return streams;
	}

	public void setStreams(List<StreamClient> streams) {
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

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
}
