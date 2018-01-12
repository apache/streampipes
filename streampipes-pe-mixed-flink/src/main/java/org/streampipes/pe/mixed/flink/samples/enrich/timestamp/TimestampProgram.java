package org.streampipes.pe.mixed.flink.samples.enrich.timestamp;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class TimestampProgram extends FlinkDataProcessorRuntime<TimestampParameters> {

	public TimestampProgram(TimestampParameters params) {
		super(params);
	}
	
	public TimestampProgram(TimestampParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		return messageStream[0]
				.flatMap(new TimestampEnricher(bindingParams.getAppendTimePropertyName()));
	}

}
