package de.fzi.cep.sepa.manager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;

import javax.script.Invocable;

public class PipelineVerificationUtils {

	/**
	 * returns the root node of a partial pipeline (a pipeline without an action)
	 * @param pipeline
	 * @return @SEPAClient
	 * @throws Exception 
	 */

	public static InvocableSEPAElement getRootNode(Pipeline pipeline) throws NoSepaInPipelineException
	{
		List<InvocableSEPAElement> elements = new ArrayList<>();
		elements.addAll(pipeline.getSepas());
		elements.addAll(pipeline.getActions());

		List<InvocableSEPAElement> unconfiguredElements = elements
				.stream()
				.filter(e -> !e.isConfigured())
				.collect(Collectors.toList());
		

		if (unconfiguredElements.size() != 1) throw new NoSepaInPipelineException();
		else return unconfiguredElements.get(0);
			
	}
}
