package de.fzi.cep.sepa.manager.monitoring.runtime;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class PipelineObserver {

	private String pipelineId;
	
	
	public PipelineObserver(String pipelineId) {
		super();
		this.pipelineId = pipelineId;
	}

	public void update() {
		System.out.println(pipelineId + " was updated yeah!!");
		Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
		Operations.stopPipeline(pipeline);
		
		
	};
	public String getPipelineId() {
		return pipelineId;
	}
	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pipelineId == null) ? 0 : pipelineId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PipelineObserver other = (PipelineObserver) obj;
		if (pipelineId == null) {
			if (other.pipelineId != null)
				return false;
		} else if (!pipelineId.equals(other.pipelineId))
			return false;
		return true;
	}
	
}
