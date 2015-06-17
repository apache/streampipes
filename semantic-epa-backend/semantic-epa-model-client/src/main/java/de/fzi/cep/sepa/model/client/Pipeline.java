package de.fzi.cep.sepa.model.client;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Pipeline extends ElementComposition {
	
	@OneToOne(cascade=CascadeType.ALL)
	private ActionClient action;
	
	@OneToOne(cascade=CascadeType.ALL)
	private boolean running;
	private long startedAt;
	
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
