package de.fzi.cep.sepa.manager.monitoring.job;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.manager.monitoring.task.GetDescriptionTask;
import de.fzi.cep.sepa.manager.monitoring.task.TaskDefinition;
import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.monitoring.TaskReport;

public abstract class MonitoringJob<T extends ConsumableSEPAElement> {

	protected T monitoredObject;	
	protected List<TaskDefinition> tasks;
	
	protected long repeatAfter;
	protected boolean success;
	
	protected String elementId;
	
	public MonitoringJob(T monitoredObject, long repeatAfter)
	{
		this.monitoredObject = monitoredObject;
		this.tasks = new ArrayList<>();
		this.repeatAfter = repeatAfter;
		this.elementId = monitoredObject.getElementId();
	}
	
	public MonitoringJob(T monitoredObject)
	{
		this.monitoredObject = monitoredObject;
		this.tasks = new ArrayList<>();
		this.repeatAfter = 600000;
	}
	
	protected void getDefinedTasks()
	{
		this.tasks.add(new GetDescriptionTask(monitoredObject));
	}
	
	public String getElementId() {
		return elementId;
	}

	public List<TaskReport> performJobExecution()
	{
		getDefinedTasks();
		List<TaskReport> reports = new ArrayList<>();
		this.tasks.forEach(t -> reports.add(t.execute()));
		
		this.success = reports.stream().anyMatch(r -> !r.isSuccess());
		
		return reports;
	}
	
	public boolean isJobExecutionSuccessful()
	{
		return success;
	}
	
	protected abstract void generateInvocableSepaElement();

}
