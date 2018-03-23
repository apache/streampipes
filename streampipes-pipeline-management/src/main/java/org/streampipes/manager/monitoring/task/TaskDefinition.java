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

package org.streampipes.manager.monitoring.task;

import java.util.Date;

import org.streampipes.model.client.monitoring.TaskReport;

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
