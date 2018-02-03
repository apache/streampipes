package org.streampipes.manager.monitoring.job;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.manager.monitoring.task.GetDescriptionTask;
import org.streampipes.manager.monitoring.task.TaskDefinition;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.client.monitoring.TaskReport;

public abstract class MonitoringJob<T extends ConsumableStreamPipesEntity> {

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
		this.elementId = monitoredObject.getUri();
	}
	
	public MonitoringJob(T monitoredObject)
	{
		this.monitoredObject = monitoredObject;
		this.tasks = new ArrayList<>();
		this.repeatAfter = 6000;
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
