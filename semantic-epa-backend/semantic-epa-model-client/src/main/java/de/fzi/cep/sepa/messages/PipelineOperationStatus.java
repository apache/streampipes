package de.fzi.cep.sepa.messages;

import java.util.ArrayList;
import java.util.List;

public class PipelineOperationStatus {

	private String pipelineId;
	private boolean success;
	
	private List<PipelineElementStatus> elementStatus;

	public PipelineOperationStatus(String pipelineId,
			List<PipelineElementStatus> elementStatus) {
		super();
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
}
