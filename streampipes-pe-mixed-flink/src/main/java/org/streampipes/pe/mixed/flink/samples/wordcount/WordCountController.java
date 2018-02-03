package org.streampipes.pe.mixed.flink.samples.wordcount;

import com.google.common.io.Resources;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.container.util.DeclarerUtils;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.mixed.flink.FlinkUtils;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class WordCountController extends FlinkDataProcessorDeclarer<WordCountParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("wordcount.jsonld"),
					DataProcessorDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public FlinkDataProcessorRuntime<WordCountParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
		return new WordCountProgram(new WordCountParameters(graph), FlinkUtils.getFlinkDeploymentConfig());
		// return new WordCountProgram(new WordCountParameters(graph));

	}
}
