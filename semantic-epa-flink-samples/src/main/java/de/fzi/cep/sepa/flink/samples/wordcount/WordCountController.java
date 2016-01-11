package de.fzi.cep.sepa.flink.samples.wordcount;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.util.DeclarerUtils;

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
