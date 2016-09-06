package de.fzi.cep.sepa.model.client.pipeline;

import de.fzi.cep.sepa.model.client.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class PipelineModificationMessage extends Message {

	/**
	 * Class that represents PipelineModification messages. Modifications are used to update a SEPA within an already created pipeline
	 */
	
	List<PipelineModification> pipelineModifications;
	
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
