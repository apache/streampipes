package de.fzi.cep.sepa.manager.monitoring.runtime;

public class PipelineObserver {

	private String pipelineId;
	
	
	public PipelineObserver(String pipelineId) {
		super();
		this.pipelineId = pipelineId;
	}

	public void update() {
		System.out.println(pipelineId + " was updated yeah!!");
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
