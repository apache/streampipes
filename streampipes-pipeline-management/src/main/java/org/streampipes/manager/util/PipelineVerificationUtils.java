package org.streampipes.manager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;

public class PipelineVerificationUtils {

	/**
	 * returns the root node of a partial pipeline (a pipeline without an action)
	 * @param pipeline
	 * @return @SEPAClient
	 * @throws Exception
	 */

	public static InvocableStreamPipesEntity getRootNode(Pipeline pipeline) throws NoSepaInPipelineException
	{
		List<InvocableStreamPipesEntity> elements = new ArrayList<>();
		elements.addAll(pipeline.getSepas());
		elements.addAll(pipeline.getActions());

		List<InvocableStreamPipesEntity> unconfiguredElements = elements
				.stream()
				.filter(e -> !e.isConfigured())
				.collect(Collectors.toList());


		if (unconfiguredElements.size() != 1) throw new NoSepaInPipelineException();
		else return unconfiguredElements.get(0);

	}
}
