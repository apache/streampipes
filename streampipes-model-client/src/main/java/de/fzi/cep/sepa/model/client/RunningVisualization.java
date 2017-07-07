package de.fzi.cep.sepa.model.client;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class RunningVisualization {
	
	private @SerializedName("_id") String id;
	private @SerializedName("_rev") String rev;
	
	private String pipelineId;
	private String pipelineName;
	private String consumerUrl;
	private String description;
	private String title;
	
	public RunningVisualization(String pipelineId, String pipelineName, String consumerUrl, String description, String title)
	{
		System.out.println("PIPE: " +pipelineId);
		this.id = UUID.randomUUID().toString();
		this.pipelineId = pipelineId;
		this.pipelineName = pipelineName;
		this.consumerUrl = consumerUrl;
		this.description = description;
		this.title = title;
	}
	
	public String getConsumerUrl() {
		return consumerUrl;
	}
	public void setConsumerUrl(String consumerUrl) {
		this.consumerUrl = consumerUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	
}
