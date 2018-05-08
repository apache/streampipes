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

package org.streampipes.model.client.pipeline;

import java.util.ArrayList;
import java.util.List;

public class PipelineOperationStatus {

	private String pipelineId;
	private String pipelineName;
	private String title;
	private boolean success;
	
	private List<PipelineElementStatus> elementStatus;

	public PipelineOperationStatus(String pipelineId, String pipelineName, String title,
			List<PipelineElementStatus> elementStatus) {
		super();
		this.title = title;
		this.pipelineName = pipelineName;
		this.pipelineId = pipelineId;
		this.elementStatus = elementStatus;
	}

	public PipelineOperationStatus() {
		this.elementStatus = new ArrayList<>();
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public List<PipelineElementStatus> getElementStatus() {
		return elementStatus;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public void setElementStatus(List<PipelineElementStatus> elementStatus) {
		this.elementStatus = elementStatus;
	}
	
	public void addPipelineElementStatus(PipelineElementStatus elementStatus)
	{
		this.elementStatus.add(elementStatus);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
}
