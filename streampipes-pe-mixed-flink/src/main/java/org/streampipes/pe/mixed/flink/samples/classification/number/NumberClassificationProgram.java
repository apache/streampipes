package org.streampipes.pe.mixed.flink.samples.classification.number;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

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
			DataStream<Map<String, Object>>... messageStream) {

		return (DataStream<Map<String, Object>>) messageStream[0]
				.flatMap(new Classifier(params.getPropertyName(), params.getDomainConceptData()));
	}

}
