package org.streampipes.pe.mixed.flink.samples.classification.number;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.io.Serializable;
import java.util.Map;

public class NumberClassificationProgram extends FlinkDataProcessorRuntime<NumberClassificationParameters>
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

		return messageStream[0]
				.flatMap(new Classifier(bindingParams.getPropertyName(), bindingParams.getDomainConceptData()));
	}

}
