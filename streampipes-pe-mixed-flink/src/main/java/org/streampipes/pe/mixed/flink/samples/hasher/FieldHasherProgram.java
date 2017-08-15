package org.streampipes.pe.mixed.flink.samples.hasher;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

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
