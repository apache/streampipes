package de.fzi.cep.sepa.manager.monitoring.task;

import java.util.Date;

import de.fzi.cep.sepa.model.client.monitoring.TaskReport;

public abstract class TaskDefinition {
	
	protected TaskReport results;
	
	public abstract void executeBefore();
	
	public abstract void executeAfter();
	
	public abstract TaskReport defineTaskExecution();
	
	public TaskReport execute()
	{
		executeBefore();
		TaskReport report = defineTaskExecution();
		executeAfter();
		return report;
	}
	
	protected TaskReport successMsg(String taskName)
	{
		return new TaskReport(taskName, new Date(), true, "");
	}
	
	protected TaskReport errorMsg(String taskName, String msg)
	{
		return new TaskReport(taskName, new Date(), false, msg);
	}
	
}
