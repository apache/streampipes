package de.fzi.cep.sepa.streampipes_flink_code_generation;


import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

import com.squareup.javapoet.ClassName;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;

/**
 * Java File Classes (JFC)
 * @author philipp
 *
 */
public abstract class JFC {

	public static ClassName MAP = ClassName.get(Map.class);

	public static ClassName STRING = ClassName.get("", "String");
	public static ClassName OVERRIDE = ClassName.get("", "Override");
	public static ClassName OBJECT = ClassName.get("", "Object");
	public static ClassName EXCEPTION = ClassName.get("", "Exception");

	public static ClassName DATA_STREAM = ClassName.get(DataStream.class);
	public static ClassName FLAT_MAP_FUNCTION = ClassName.get(FlatMapFunction.class);
	public static ClassName COLLECTOR = ClassName.get(Collector.class);

	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get(FlinkDeploymentConfig.class);
	public static ClassName FLINK_SEPA_RUNTIME = ClassName.get(FlinkSepaRuntime.class);
	public static ClassName ABSTRACT_FLINK_AGENT_DECLARER = ClassName.get(AbstractFlinkAgentDeclarer.class);
}
