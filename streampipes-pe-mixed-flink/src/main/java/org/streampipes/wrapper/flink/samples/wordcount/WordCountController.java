package org.streampipes.wrapper.flink.samples.wordcount;

import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.Config;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.container.util.DeclarerUtils;

public class WordCountController extends AbstractFlinkAgentDeclarer<WordCountParameters> {

	@Override
	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("wordcount.jsonld"),
					SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected FlinkSepaRuntime<WordCountParameters> getRuntime(SepaInvocation graph) {
		return new WordCountProgram(new WordCountParameters(graph),
				new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
		// return new WordCountProgram(new WordCountParameters(graph));

	}
}
