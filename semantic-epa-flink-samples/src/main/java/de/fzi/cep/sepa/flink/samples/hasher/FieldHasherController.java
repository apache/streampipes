package de.fzi.cep.sepa.flink.samples.hasher;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class FieldHasherController extends AbstractFlinkAgentDeclarer<FieldHasherParameters>{

	@Override
	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("fieldhasher.jsonld"),
					SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected FlinkSepaRuntime<FieldHasherParameters> getRuntime(
			SepaInvocation graph) {
		String propertyName = SepaUtils.getMappingPropertyName(graph, "property-mapping");
		
		HashAlgorithmType hashAlgorithmType = HashAlgorithmType.valueOf(SepaUtils.getOneOfProperty(graph, "hash-algorithm"));
		
		return new FieldHasherProgram(
				new FieldHasherParameters(graph, propertyName, hashAlgorithmType),
				new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
	}

}
