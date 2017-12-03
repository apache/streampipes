package org.streampipes.pe.mixed.flink.samples.wordcount;

import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.container.util.DeclarerUtils;

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
	protected FlinkDataProcessorRuntime<WordCountParameters> getRuntime(DataProcessorInvocation graph) {
		return new WordCountProgram(new WordCountParameters(graph),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
		// return new WordCountProgram(new WordCountParameters(graph));

	}
}
