package de.fzi.cep.sepa.model.client;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ExecutionStatus {

	boolean running;
	Date startedAt;
	
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public Date getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}
	
	
}
