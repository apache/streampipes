package de.fzi.cep.sepa.flink.converter;

import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class ObjectToMapConverter<T extends Object> implements FlatMapFunction<T, Map<String, Object>>{

	@Override
	public void flatMap(T in, Collector<Map<String, Object>> out)
			throws Exception {
		out.collect(new org.apache.commons.beanutils.BeanMap(in));
		
	}

}
