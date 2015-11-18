package de.fzi.cep.sepa.flink.samples.wordcount;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.flink.AbstractFlinkDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class WordCountController extends AbstractFlinkDeclarer<WordCountParameters> {

	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("wordcount.jsonLd"), SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected FlinkSepaRuntime<WordCountParameters, ?> getRuntime(
			SepaInvocation graph) {
		return new WordCountTopology(new WordCountParameters(graph), new FlinkDeploymentConfig("semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar", "ipe-koi05.fzi.de", 6123));
		//return new WordCountTopology(new WordCountParameters(graph));

	}
}
