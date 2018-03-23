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

import com.google.gson.annotations.SerializedName;
import org.streampipes.model.graph.DataSinkInvocation;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pipeline extends ElementComposition {
	
	@OneToOne(cascade=CascadeType.ALL)
	private List<DataSinkInvocation> actions;
	
	@OneToOne(cascade=CascadeType.ALL)
	private boolean running;
	private long startedAt;
	private long createdAt;
	
	private boolean publicElement;
	
	private String createdByUser;
	
	private List<String> pipelineCategories;
	
	private @SerializedName("_id") String pipelineId;
    private @SerializedName("_rev") String rev;

	public Pipeline() {
		super();
		this.actions = new ArrayList<>();
	}

	public List<DataSinkInvocation> getActions() {
		return actions;
	}

	public void setActions(List<DataSinkInvocation> actions) {
		this.actions = actions;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(long startedAt) {
		this.startedAt = startedAt;
	}
	
	public boolean isPublicElement() {
		return publicElement;
	}

	public void setPublicElement(boolean publicElement) {
		this.publicElement = publicElement;
	}

	
	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}
	
	

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public List<String> getPipelineCategories() {
		return pipelineCategories;
	}

	public void setPipelineCategories(List<String> pipelineCategories) {
		this.pipelineCategories = pipelineCategories;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Pipeline clone()
	{
		Pipeline pipeline = new Pipeline();
		pipeline.setName(name);
		pipeline.setDescription(description);
		pipeline.setSepas(sepas);
		pipeline.setStreams(streams);
		pipeline.setActions(actions);
		pipeline.setCreatedByUser(createdByUser);
		pipeline.setPipelineCategories(pipelineCategories);
		pipeline.setCreatedAt(createdAt);
		pipeline.setPipelineId(pipelineId);
		pipeline.setRev(rev);
		return pipeline;
	}
	
	
}
