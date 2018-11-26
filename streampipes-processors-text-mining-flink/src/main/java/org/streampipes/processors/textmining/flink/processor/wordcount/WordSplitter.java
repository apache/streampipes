package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class WordSplitter implements FlatMapFunction<Map<String, Object>, Word> {

	private String mappingPropertyName;
	
	public WordSplitter(String mappingPropertyName) {
		this.mappingPropertyName = mappingPropertyName;
	}
	
	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Word> out) throws Exception {
		
		String propertyValue = (String) in.get(mappingPropertyName);
		for(String word : propertyValue.split(" "))
		{
			out.collect(new Word(word, 1));
		}
	}

	

}
