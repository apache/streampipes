package de.fzi.cep.sepa.flink.samples.enrich.timestamp;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class TimestampProgram extends FlinkSepaRuntime<TimestampParameters>{

	public TimestampProgram(TimestampParameters params) {
		super(params);
	}
	
	public TimestampProgram(TimestampParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		return (DataStream<Map<String, Object>>) messageStream[0]
				.flatMap(new TimestampEnricher(params.getAppendTimePropertyName()));
	}

}
