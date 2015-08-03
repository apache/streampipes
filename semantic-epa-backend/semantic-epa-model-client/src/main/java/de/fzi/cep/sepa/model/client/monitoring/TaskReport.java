package de.fzi.cep.sepa.model.client.monitoring;

import java.util.Date;

public class TaskReport {
	//contains report of an individual task
	private Date executionDate;
	private boolean success;
	private String taskName;
	
	private String optionalMessage;

	public TaskReport(String taskName, Date executionDate, boolean success,
			String optinalMessage) {
		super();
		this.executionDate = executionDate;
		this.success = success;
		this.optionalMessage = optinalMessage;
		this.taskName = taskName;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}	
	
	@Override
	public String toString()
	{
		return "{ \nTask Execution: " +taskName +"\nDate: " +executionDate.toString() +"\nSuccess: " +success + "\nMessage: " +optionalMessage +"}";
	}
}
