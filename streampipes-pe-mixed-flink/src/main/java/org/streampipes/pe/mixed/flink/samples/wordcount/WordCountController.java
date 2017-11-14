package org.streampipes.pe.mixed.flink.samples.wordcount;

import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.container.util.DeclarerUtils;

public class WordCountController extends AbstractFlinkAgentDeclarer<WordCountParameters> {

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
	protected FlinkSepaRuntime<WordCountParameters> getRuntime(DataProcessorInvocation graph) {
		return new WordCountProgram(new WordCountParameters(graph),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
		// return new WordCountProgram(new WordCountParameters(graph));

	}
}
