package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

public class ConfigurableTimestampProgram extends FlinkSepaRuntime<ConfigurableTimestampParameters>{

	public ConfigurableTimestampProgram(ConfigurableTimestampParameters params) {
		super(params);
	}
	
	public ConfigurableTimestampProgram(ConfigurableTimestampParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		return (DataStream<Map<String, Object>>) messageStream[0]
				.flatMap(new ConfigurableTimestampEnricher(params.getAppendTimePropertyName()));
	}

}
