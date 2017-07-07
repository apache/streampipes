package org.streampipes.wrapper.flink.samples.rename;

import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class FieldRenamer implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

	private static final long serialVersionUID = 1L;
	
	private String oldPropertyName;
	private String newPropertyName;
	
	public FieldRenamer(String oldPropertyName, String newPropertyName) {
		this.oldPropertyName = oldPropertyName;
		this.newPropertyName = newPropertyName;
	}
	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		Object propertyValue = in.get(oldPropertyName);
		in.remove(oldPropertyName);
		in.put(newPropertyName, propertyValue);
		out.collect(in);
	}

}
