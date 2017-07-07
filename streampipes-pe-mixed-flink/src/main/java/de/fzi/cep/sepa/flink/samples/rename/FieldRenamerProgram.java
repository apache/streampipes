package de.fzi.cep.sepa.flink.samples.rename;

import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;

public class FieldRenamerProgram extends FlinkSepaRuntime<FieldRenamerParameters> {

	public FieldRenamerProgram(FieldRenamerParameters params,
			FlinkDeploymentConfig config) {
		super(params, config);
	}
	
	public FieldRenamerProgram(FieldRenamerParameters params) {
		super(params);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		return messageStream[0].flatMap(new FieldRenamer(params.getOldPropertyName(),
				params.getNewPropertyName()));
	}
}
