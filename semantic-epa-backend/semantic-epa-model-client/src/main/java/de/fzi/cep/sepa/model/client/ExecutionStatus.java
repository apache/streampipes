package de.fzi.cep.sepa.model.client;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ExecutionStatus {

	boolean isRunning;
	Date startedAt;
	
	
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public Date getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}
	
	
}
