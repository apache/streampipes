package de.fzi.cep.sepa.flink.samples.classification.number;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;

public class NumberClassificationProgram extends FlinkSepaRuntime<NumberClassificationParameters>
		implements Serializable {

	public NumberClassificationProgram(NumberClassificationParameters params) {
		super(params);
	}

	public NumberClassificationProgram(NumberClassificationParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>> messageStream) {

		return (DataStream<Map<String, Object>>) messageStream
				.flatMap(new Classifier(params.getPropertyName(), params.getDomainConceptData()));
	}

}
