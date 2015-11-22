package de.fzi.cep.sepa.flink.samples.elasticsearch;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.flink.AbstractFlinkConsumerDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSecRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class ElasticSearchController extends AbstractFlinkConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("elasticsearch.jsonld"), SecDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FlinkSecRuntime getRuntime(SecInvocation graph) {
		return new ElasticSearchProgram(graph, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
		//return new ElasticSearchProgram(graph);
	}

}
