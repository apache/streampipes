package de.fzi.cep.sepa.model.client;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Pipeline {

	@OneToMany(cascade=CascadeType.ALL)
	private List<SEPAClient> sepas;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<StreamClient> streams;
	
	@OneToOne(cascade=CascadeType.ALL)
	private ActionClient action;
	
	@OneToOne(cascade=CascadeType.ALL)
	private boolean running;
	private long startedAt;
	
	private String name;
	private String description;
	
	private @SerializedName("_id") String pipelineId;
	private @SerializedName("_rev") String rev;

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

	public ActionClient getAction() {
		return action;
	}

	public void setAction(ActionClient action) {
		this.action = action;
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(long startedAt) {
		this.startedAt = startedAt;
	}
	
	public Pipeline clone()
	{
		Pipeline pipeline = new Pipeline();
		pipeline.setName(name);
		pipeline.setDescription(description);
		pipeline.setSepas(sepas);
		pipeline.setStreams(streams);
		pipeline.setAction(action);
		
		return pipeline;
	}
	
}
