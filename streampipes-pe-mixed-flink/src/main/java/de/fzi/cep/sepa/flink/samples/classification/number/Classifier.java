package de.fzi.cep.sepa.flink.samples.classification.number;

import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class Classifier implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1459514638462383461L;

	private String  propertyName;
	private List<DataClassification> classes;
	
	public Classifier(String propertyName, List<DataClassification> classes) {
		this.propertyName = propertyName;
		this.classes = classes;
	}

	@Override
	public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
		Object o = in.get(propertyName);
		double value;
		if (o instanceof Integer) {
			value = ((Integer) o).doubleValue();
		} else {
			value = (double) o;
		}
		
		for (DataClassification c : classes) {
			if (value >= c.getMinValue() && value <= c.getMaxValue()) {
				in.put("output_label", c.getLabel());
				out.collect(in);
			}
		}


	}

}
