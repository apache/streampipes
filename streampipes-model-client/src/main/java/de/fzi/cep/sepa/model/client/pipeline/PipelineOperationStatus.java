package de.fzi.cep.sepa.model.client.pipeline;

import java.util.ArrayList;
import java.util.List;

public class PipelineOperationStatus {

	private String pipelineId;
	private String pipelineName;
	private String title;
	private boolean success;
	
	private List<PipelineElementStatus> elementStatus;

	public PipelineOperationStatus(String pipelineId, String pipelineName, String title,
			List<PipelineElementStatus> elementStatus) {
		super();
		this.title = title;
		this.pipelineName = pipelineName;
		this.pipelineId = pipelineId;
		this.elementStatus = elementStatus;
	}

	public PipelineOperationStatus() {
		this.elementStatus = new ArrayList<>();
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public List<PipelineElementStatus> getElementStatus() {
		return elementStatus;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public void setElementStatus(List<PipelineElementStatus> elementStatus) {
		this.elementStatus = elementStatus;
	}
	
	public void addPipelineElementStatus(PipelineElementStatus elementStatus)
	{
		this.elementStatus.add(elementStatus);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
}
