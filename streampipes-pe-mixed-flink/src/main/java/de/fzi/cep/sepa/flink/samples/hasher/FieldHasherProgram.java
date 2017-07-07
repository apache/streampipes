package de.fzi.cep.sepa.flink.samples.hasher;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;

public class FieldHasherProgram extends FlinkSepaRuntime<FieldHasherParameters> 
	implements Serializable{

	public FieldHasherProgram(FieldHasherParameters params,
			FlinkDeploymentConfig config) {
		super(params, config);
	}
	
	public FieldHasherProgram(FieldHasherParameters params) {
		super(params);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		return messageStream[0].flatMap(new FieldHasher(params.getPropertyName(),
				params.getHashAlgorithmType().hashAlgorithm()));
	}

}
