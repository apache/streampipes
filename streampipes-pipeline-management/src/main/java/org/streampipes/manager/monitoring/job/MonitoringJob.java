/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
