package de.fzi.cep.sepa.model.client;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.google.gson.annotations.SerializedName;

@Entity
public class Pipeline extends ElementComposition {
	
	@OneToOne(cascade=CascadeType.ALL)
	private ActionClient action;
	
	@OneToOne(cascade=CascadeType.ALL)
	private boolean running;
	private long startedAt;
	
	private boolean publicElement;
	
	private String createdByUser;
	
	private @SerializedName("_id") String pipelineId;
    private @SerializedName("_rev") String rev;
	
	public ActionClient getAction() {
		return action;
	}

	public void setAction(ActionClient action) {
		this.action = action;
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
	
	public boolean isPublicElement() {
		return publicElement;
	}

	public void setPublicElement(boolean publicElement) {
		this.publicElement = publicElement;
	}

	
	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
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

	public Pipeline clone()
	{
		Pipeline pipeline = new Pipeline();
		pipeline.setName(name);
		pipeline.setDescription(description);
		pipeline.setSepas(sepas);
		pipeline.setStreams(streams);
		pipeline.setAction(action);
		pipeline.setCreatedByUser(createdByUser);
		
		return pipeline;
	}
	
	
}
