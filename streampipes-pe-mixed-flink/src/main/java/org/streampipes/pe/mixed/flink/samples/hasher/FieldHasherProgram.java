package org.streampipes.pe.mixed.flink.samples.hasher;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.io.Serializable;
import java.util.Map;

public class FieldHasherProgram extends FlinkDataProcessorRuntime<FieldHasherParameters>
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
		return messageStream[0].flatMap(new FieldHasher(bindingParams.getPropertyName(),
				bindingParams.getHashAlgorithmType().hashAlgorithm()));
	}

}
