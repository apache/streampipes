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

import org.streampipes.model.client.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class PipelineModificationMessage extends Message {

	/**
	 * Class that represents PipelineModification messages. Modifications are used to update a SEPA within an already created preprocessing
	 */
	
	private List<PipelineModification> pipelineModifications;
	
	public PipelineModificationMessage(
			List<PipelineModification> pipelineModifications) {
		super(true);
		this.pipelineModifications = pipelineModifications;
	}

	public PipelineModificationMessage() {
		super(true);
		pipelineModifications = new ArrayList<>();
	}

	public List<PipelineModification> getPipelineModifications() {
		return pipelineModifications;
	}

	public void setPipelineModifications(
			List<PipelineModification> pipelineModifications) {
		this.pipelineModifications = pipelineModifications;
	}
	
	public void addPipelineModification(PipelineModification pipelineModification)
	{
		pipelineModifications.add(pipelineModification);
	}
	
	public boolean existsModification(String domId)
	{
		for(PipelineModification modification : pipelineModifications)
		{
			if (modification.getDomId().contains(domId))
				return true;
		}
		return false;
	}
	
	
}
