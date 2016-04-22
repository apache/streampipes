package de.fzi.cep.sepa.streampipes_flink_code_generation;

import com.squareup.javapoet.ClassName;

/**
 * Java File Classes (JFC)
 * @author philipp
 *
 */
public abstract class JFC {

	public static ClassName MAP = ClassName.get("java.util", "Map");

	public static ClassName STRING = ClassName.get("", "String");
	public static ClassName OVERRIDE = ClassName.get("", "Override");
	public static ClassName OBJECT = ClassName.get("", "Object");
	public static ClassName EXCEPTION = ClassName.get("", "Exception");

	public static ClassName DATA_STREAM = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStream");
	public static ClassName FLAT_MAP_FUNCTION = ClassName.get("org.apache.flink.api.common.functions", "FlatMapFunction");
	public static ClassName COLLECTOR = ClassName.get("org.apache.flink.util", "Collector");

//	public static ClassName RESOURCES = ClassName.get("com.google.common.io", "Resources");

	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get("de.fzi.cep.sepa.flink", "FlinkDeploymentConfig");
	public static ClassName FLINK_SEPA_RUNTIME = ClassName.get("de.fzi.cep.sepa.flink", "FlinkSepaRuntime");
//	public static ClassName SEPA_PARSE_EXCEPTION = ClassName.get("de.fzi.cep.sepa.commons.exceptions", "SepaParseException");
//	public static ClassName DECLARE_UTILS = ClassName.get("de.fzi.cep.sepa.util", "DeclarerUtils");
}
