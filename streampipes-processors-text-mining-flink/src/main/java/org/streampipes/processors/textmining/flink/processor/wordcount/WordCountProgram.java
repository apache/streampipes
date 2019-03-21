package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.textmining.flink.AbstractTextMiningProgram;

import java.io.Serializable;

public class WordCountProgram extends AbstractTextMiningProgram<WordCountParameters> implements Serializable {

  public WordCountProgram(WordCountParameters params, boolean debug) {
    super(params, debug);
  }

  public WordCountProgram(WordCountParameters params) {
    super(params);
  }

  @Override
  protected DataStream<Event> getApplicationLogic(
          DataStream<Event>... messageStream) {

    return messageStream[0]
            .flatMap(new WordSplitter(bindingParams.getWordCountFieldName()))
            .keyBy("word")
            .sum("count")
            .flatMap(new WordToEventConverter());
  }

}
