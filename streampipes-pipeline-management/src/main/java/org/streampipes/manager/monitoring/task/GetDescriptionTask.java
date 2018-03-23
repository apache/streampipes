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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import org.streampipes.manager.monitoring.job.MonitoringUtils;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.client.monitoring.TaskReport;

public class GetDescriptionTask extends TaskDefinition {
	
	private ConsumableStreamPipesEntity element;
	
	private static final String TASK_NAME = "HTTP Get Availability consul";
	
	public GetDescriptionTask(ConsumableStreamPipesEntity element)
	{
		super();
		this.element = element;
	}
	

	@Override
	public void executeBefore() {
		// TODO Auto-generated method stub
	}

	@Override
	public void executeAfter() {
		// TODO Auto-generated method stub
	}

	@Override
	public TaskReport defineTaskExecution() {
		try {
			int statusCode = MonitoringUtils.getHttpResponse(element.getUri()).getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return successMsg(TASK_NAME);
			}
			else
			{
				return errorMsg(TASK_NAME, "Wrong status code");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return errorMsg(TASK_NAME, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return errorMsg(TASK_NAME, e.getMessage());
		}
	}
}
