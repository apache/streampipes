package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;

public class WordSplitter implements FlatMapFunction<Event, Word> {

  private String mappingPropertyName;

  public WordSplitter(String mappingPropertyName) {
    this.mappingPropertyName = mappingPropertyName;
  }

  @Override
  public void flatMap(Event in,
                      Collector<Word> out) throws Exception {

    String propertyValue = in.getFieldBySelector(mappingPropertyName).getAsPrimitive().getAsString();
    for (String word : propertyValue.split(" ")) {
      out.collect(new Word(word, 1));
    }
  }

}
