package de.fzi.cep.sepa.manager.monitoring.runtime;

import de.fzi.cep.sepa.manager.execution.status.PipelineStatusManager;
import de.fzi.cep.sepa.model.client.pipeline.PipelineStatusMessage;
import de.fzi.cep.sepa.model.client.pipeline.PipelineStatusMessageType;

public class PipelineObserver {

	private String pipelineId;
	
	
	public PipelineObserver(String pipelineId) {
		super();
		this.pipelineId = pipelineId;
	}

	public void update() {
		System.out.println(pipelineId + " was updated yeah!!");
		PipelineStatusManager.addPipelineStatus(pipelineId, makePipelineStatusMessage(PipelineStatusMessageType.PIPELINE_NO_DATA));
		
		SimilarStreamFinder streamFinder = new SimilarStreamFinder(pipelineId);
		if (streamFinder.isReplacable()) {
			System.out.println("Pipeline replacable");
			
			boolean success = new PipelineStreamReplacer(pipelineId, streamFinder.getSimilarStreams().get(0)).replaceStream();
			if (success) {
				System.out.println("success");
				PipelineStatusManager.addPipelineStatus(pipelineId, makePipelineStatusMessage(PipelineStatusMessageType.PIPELINE_EXCHANGE_SUCCESS));
			} else {
				System.out.println("failure");
				PipelineStatusManager.addPipelineStatus(pipelineId, makePipelineStatusMessage(PipelineStatusMessageType.PIPELINE_EXCHANGE_FAILURE));
			}
		} else {
			System.out.println("Pipeline not replacable");
			PipelineStatusManager.addPipelineStatus(pipelineId, makePipelineStatusMessage(PipelineStatusMessageType.PIPELINE_EXCHANGE_FAILURE));
		}
	};
	
	private PipelineStatusMessage makePipelineStatusMessage(PipelineStatusMessageType type) {
		return new PipelineStatusMessage(pipelineId, System.currentTimeMillis(), type.title(), type.description());
	}
	
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
